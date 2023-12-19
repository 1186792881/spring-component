package com.wangyi.component.i18n.util;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.i18n.config.properties.I18nProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class I18nCacheUtil {

    private final I18nProperties i18nProperties;
    // key: i18n:result_code:zh-CN:example.00001
    private final LFUCache<String, String> localCache;

    public I18nCacheUtil(I18nProperties i18nProperties) {
        this.i18nProperties = i18nProperties;
        this.localCache = new LFUCache<>(i18nProperties.getLocalCacheCapacity());
    }

    /**
     * 从本地缓存获取信息
     *
     * @param type
     * @param language
     * @param codeList
     * @return Pair key-> unCacheCodeList, value -> codeMsgMap
     */
    public Pair<List<String>, Map<String, String>> getCache(String type, String language, List<String> codeList) {

        Map<String, String> map = new HashMap<>(codeList.size());

        if (!i18nProperties.getEnableLocalCache() || StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeList)) {
            return Pair.of(codeList, map);
        }

        // 先从缓存获取
        List<String> unCacheCodeList = new ArrayList<>();
        codeList.forEach(code -> {
            String localCacheKey = i18nProperties.getCacheKey(type, language, code);
            String value = localCache.get(localCacheKey, false);
            if (null != value) {
                map.put(code, value);
            } else {
                unCacheCodeList.add(code);
            }
        });

        return Pair.of(unCacheCodeList, map);

    }

    /**
     * 本地缓存国际化信息
     *
     * @param type
     * @param language
     * @param codeMsgMap
     */
    public void putCache(String type, String language, Map<String, String> codeMsgMap) {
        if (!i18nProperties.getEnableLocalCache() || StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeMsgMap)) {
            return;
        }

        codeMsgMap.forEach((code, msg) -> {
            String localCacheKey = i18nProperties.getCacheKey(type, language, code);
            String value = localCache.get(localCacheKey, false);
            if (null != value) {
                localCache.put(localCacheKey, value, i18nProperties.getLocalCacheTimeOut());
            }
        });
    }

}
