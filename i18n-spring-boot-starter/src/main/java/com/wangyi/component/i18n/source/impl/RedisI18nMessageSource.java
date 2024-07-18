package com.wangyi.component.i18n.source.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.i18n.config.properties.I18nProperties;
import com.wangyi.component.i18n.source.I18nMessageSource;
import com.wangyi.component.i18n.source.entity.I18n;
import com.wangyi.component.i18n.util.I18nCacheUtil;
import com.wangyi.component.i18n.util.ScanUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 国际化信息保存到redis
 * 从 redis 中获取国际化信息
 */
@ConditionalOnProperty(value = "i18n.storage", havingValue = "redis")
@Configuration
@Lazy
public class RedisI18nMessageSource implements I18nMessageSource {

    private final StringRedisTemplate stringRedisTemplate;
    private final I18nProperties i18nProperties;
    private final I18nCacheUtil i18nCacheUtil;

    public RedisI18nMessageSource(StringRedisTemplate stringRedisTemplate, I18nProperties i18nProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.i18nProperties = i18nProperties;
        this.i18nCacheUtil = new I18nCacheUtil(i18nProperties);
    }

    @Override
    public String getMessage(String type, String language, String code) {
        if (StrUtil.hasBlank(type, code, language)) {
            return null;
        }

        Map<String, String> msgMap = getMessage(type, language, CollUtil.newArrayList(code));
        return msgMap.get(code);
    }

    @Override
    public Map<String, String> getMessage(String type, String language, List<String> codeList) {
        if (StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeList)) {
            return Collections.emptyMap();
        }

        // 先从本地缓存获取
        Pair<List<String>, Map<String, String>> pair = i18nCacheUtil.getCache(type, language, codeList);
        List<String> unCacheCodeList = pair.getKey();
        Map<String, String> map = pair.getValue();
        if (CollUtil.isEmpty(unCacheCodeList)) {
            return map;
        }

        // 从redis取, 并加入本地缓存
        String redisKey = StrUtil.join(StrUtil.COLON, i18nProperties.getI18nStoragePrefix(), type, language);
        List<Object> msgList = stringRedisTemplate.opsForHash().multiGet(redisKey, new ArrayList<>(unCacheCodeList));
        Map<String, String> unCacheMap = new HashMap<>(msgList.size());
        for (int i = 0; i < unCacheCodeList.size(); i++) {
            String code = codeList.get(i);
            String msg = (String) msgList.get(i);
            unCacheMap.put(code, msg);
        }
        i18nCacheUtil.putCache(type, language, unCacheMap);

        // 返回所有查询到的国际化信息
        map.putAll(unCacheMap);
        return map;
    }

    @Override
    public void clearLocalCache() {
        i18nCacheUtil.clearCache();
    }

    @Override
    public void initMessage() {
        // key->i18n:result_code:zh-CN, value->{code, msg}
        List<I18n> i18nList = ScanUtil.scanI18nEnum(i18nProperties);
        if (CollUtil.isEmpty(i18nList)) {
            return;
        }

        Map<String, Map<String, String>> typeCodeMsgMap = new HashMap<>();
        i18nList.forEach(i18n -> {
            String type = i18n.getType();
            String language = i18n.getLanguage();
            String code = i18n.getCode();
            String value = i18n.getValue();

            String redisKey = StrUtil.join(StrPool.COLON, i18nProperties.getI18nStoragePrefix(), type, language);
            Map<String, String> codeMsgMap = typeCodeMsgMap.computeIfAbsent(redisKey, k -> new HashMap<>());
            codeMsgMap.put(code, value);
        });

        typeCodeMsgMap.forEach((redisKey, codeMsgMap) -> {
            if (MapUtil.isNotEmpty(codeMsgMap)) {
                stringRedisTemplate.opsForHash().putAll(redisKey, codeMsgMap);
            }
        });
    }

}
