package com.wangyi.component.i18n.source;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.i18n.constant.I18nConstant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisI18nMessageSource implements I18nMessageSource {

    private final StringRedisTemplate stringRedisTemplate;
    private final LFUCache<String, String> localCache = new LFUCache<>(2000);

    public RedisI18nMessageSource(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String getMessage(String type, String code, String language) {
        if (StrUtil.isBlank(type) || StrUtil.isBlank(code) || StrUtil.isBlank(language)) {
            return null;
        }
        String redisKey = I18nConstant.I18N_KEY + type + ":" + language;
        String localCacheKey = redisKey + ":" + code;
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
        if (StrUtil.isBlank(type) || CollUtil.isEmpty(codeList) || StrUtil.isBlank(language)) {
            return map;
        }

        String redisKey = I18nConstant.I18N_KEY + type + ":" + language;
        List<Object> msgList = stringRedisTemplate.opsForHash().multiGet(redisKey, new ArrayList<>(codeList));
        for (int i = 0; i < codeList.size(); i++) {
            String code = codeList.get(i);
            String msg = (String) msgList.get(i);
            map.put(code, msg);
        }

        return map;
    }

    @Override
    public void initMessage(String type, Map<String, String> codeMsgMap, String language) {
        String redisKey = I18nConstant.I18N_KEY + type + ":" + language;
        stringRedisTemplate.opsForHash().putAll(redisKey, codeMsgMap);
    }

}
