package com.wangyi.component.i18n.core;

import cn.hutool.cache.impl.LFUCache;
import com.wangyi.component.i18n.constant.I18nConstant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

public class RedisI18nMessageSource implements I18nMessageSource {

    private final StringRedisTemplate stringRedisTemplate;
    private final LFUCache<String, String> localCache = new LFUCache<>(2000);

    public RedisI18nMessageSource(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String getMessage(String type, String code, String language) {
        String redisKey = I18nConstant.I18N_KEY + type + ":" + language;
        String localCacheKey = redisKey + ":" + code;
        String msg = localCache.get(localCacheKey, false);
        if (null == msg) {
            msg = (String) stringRedisTemplate.opsForHash().get(redisKey, code);
            if (null != msg) {
                localCache.put(localCacheKey, msg, 30 * 60 * 1000);
            }
        }
        return msg;
    }

    @Override
    public void initMessage(String type, Map<String, String> codeMsgMap, String language) {
        String redisKey = I18nConstant.I18N_KEY + type + ":" + language;
        stringRedisTemplate.opsForHash().putAll(redisKey, codeMsgMap);
    }

}
