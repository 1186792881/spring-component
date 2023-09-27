package com.wangyi.component.i18n.source;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.base.exception.BizAssert;
import com.wangyi.component.i18n.annotation.I18nType;
import com.wangyi.component.i18n.config.I18nProperties;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 国际化信息保存到redis
 * 从 redis 中获取国际化信息
 */
@ConditionalOnProperty(value = "i18n.storage", havingValue = "redis")
@Lazy
@Configuration
public class RedisI18nMessageSource implements I18nMessageSource {

    private final StringRedisTemplate stringRedisTemplate;
    private final I18nProperties i18nProperties;

    // key: i18n:result_code:zh-CN:example.00001
    private final LFUCache<String, String> localCache = new LFUCache<>(2000);

    public RedisI18nMessageSource(StringRedisTemplate stringRedisTemplate, I18nProperties i18nProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.i18nProperties = i18nProperties;
    }

    @Override
    public String getMessage(String type, String code, String language) {
        if (StrUtil.hasBlank(type, code, language)) {
            return null;
        }
        String redisKey = StrUtil.join(StrUtil.COLON, i18nProperties.getI18nStoragePrefix(), type, language);
        String localCacheKey = StrUtil.join(StrUtil.COLON, redisKey, code);
        String msg = localCache.get(localCacheKey, false);
        if (null == msg) {
            Map<String, String> msgMap = getMessage(type, CollUtil.newArrayList(code), language);
            msg = msgMap.get(code);
            if (null != msg) {
                localCache.put(localCacheKey, msg, 30 * 60 * 1000);
            }
        }
        return msg;
    }

    @Override
    public Map<String, String> getMessage(String type, List<String> codeList, String language) {
        Map<String, String> map = new HashMap<>();
        if (StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeList)) {
            return map;
        }

        String redisKey = StrUtil.join(StrUtil.COLON, i18nProperties.getI18nStoragePrefix(), type, language);
        List<Object> msgList = stringRedisTemplate.opsForHash().multiGet(redisKey, new ArrayList<>(codeList));
        for (int i = 0; i < codeList.size(); i++) {
            String code = codeList.get(i);
            String msg = (String) msgList.get(i);
            map.put(code, msg);
        }

        return map;
    }

    @Override
    public void initMessage() {
        // key:  redisKey,  value: (code, msg)
        Map<String, Map<String, String>> typeCodeMsgMap = scanI18nEnum();
        if (MapUtil.isEmpty(typeCodeMsgMap)) {
            return;
        }
        typeCodeMsgMap.forEach((redisKey, codeMsgMap) -> {
            if (MapUtil.isNotEmpty(codeMsgMap)) {
                stringRedisTemplate.opsForHash().putAll(redisKey, codeMsgMap);
            }
        });
    }

    @SneakyThrows
    private Map<String, Map<String, String>> scanI18nEnum() {
        Map<String, Map<String, String>> typeCodeMsgMap = new HashMap<>();

        if (!i18nProperties.isEnableInit() || StrUtil.isBlank(i18nProperties.getScanPackage())) {
            return typeCodeMsgMap;
        }


        Set<Class<?>> set = ClassUtil.scanPackageByAnnotation(i18nProperties.getScanPackage(), I18nType.class);
        for (Class<?> clazz : set) {
            I18nType i18nType = AnnotationUtils.findAnnotation(clazz, I18nType.class);
            if (null != i18nType && ClassUtil.isEnum(clazz)) {
                Method codeMethod = ClassUtil.getDeclaredMethod(clazz, i18nType.codeMethod());
                Method msgMethod = ClassUtil.getDeclaredMethod(clazz, i18nType.msgMethod());
                if (codeMethod == null || msgMethod == null) {
                    continue;
                }

                // 得到 enum 的所有实例：一个数组，该数组包含组成此 Class 对象表示的枚举类的值
                Map<String, String> codeMsgMap = new HashMap<>();
                Object[] objs = clazz.getEnumConstants();
                for (Object obj : objs) {
                    String code = (String) codeMethod.invoke(obj);
                    String msg = (String) msgMethod.invoke(obj);
                    BizAssert.isTrue(codeMsgMap.containsKey(code), "i18n.init.error", "错误码重复 code:{}, msg:{}", code, msg);
                    codeMsgMap.put(code, msg);
                }

                String redisKey = StrUtil.join(StrUtil.COLON, i18nProperties.getI18nStoragePrefix(), i18nType.value().getValue(), i18nType.language().getValue());
                typeCodeMsgMap.put(redisKey, codeMsgMap);
            }
        }

        return typeCodeMsgMap;
    }

}
