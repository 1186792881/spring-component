package com.wangyi.component.i18n.source.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.wangyi.component.i18n.config.properties.I18nProperties;
import com.wangyi.component.i18n.source.I18nMessageSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@ConditionalOnBean(NacosConfigManager.class)
@ConditionalOnProperty(value = "i18n.storage", havingValue = "nacos")
@Configuration
@Lazy
@Slf4j
public class NacosI18nMessageSource implements I18nMessageSource {

    private final I18nProperties i18nProperties;
    private final NacosConfigManager nacosConfigManager;
    private final String dataIdExt = ".properties";

    // key: result_code:zh-CN,  value: <code, msg>
    private final Map<String, Map<String, String>> typeCodeMsgMap = new ConcurrentHashMap<>();

    public NacosI18nMessageSource(NacosConfigManager nacosConfigManager, I18nProperties i18nProperties) {
        this.nacosConfigManager = nacosConfigManager;
        this.i18nProperties = i18nProperties;
    }

    @Override
    public String getMessage(String type, String language, String code) {
        if (StrUtil.hasBlank(type, code, language)) {
            return null;
        }

        Map<String, String> map = getMessage(type, language, ListUtil.toList(code));
        return map.get(code);
    }

    @Override
    public Map<String, String> getMessage(String type, String language, List<String> codeList) {
        if (StrUtil.hasBlank(type, language) || CollUtil.isEmpty(codeList)) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        if (MapUtil.isEmpty(typeCodeMsgMap)) {
            return map;
        }
        for (String code : codeList) {
            String key = StrUtil.join(StrPool.COLON, type, language);
            Map<String, String> codeMsgMap = typeCodeMsgMap.get(key);
            if (MapUtil.isEmpty(codeMsgMap)) {
                continue;
            }
            map.put(code, codeMsgMap.get(code));
        }
        return map;
    }

    @SneakyThrows
    @Override
    public void initMessage() {
        String group = i18nProperties.getNacos().getI18nGroup();
        List<String> dataIdList = i18nProperties.getNacos().getI18nDataIdList();
        if (CollUtil.isEmpty(dataIdList)) {
            return;
        }

        for (String dataId : dataIdList) {
            // 首次初始化 typeCodeMsgMap
            String content = nacosConfigManager.getConfigService().getConfig(dataId, group, 5000L);
            refreshConfig(dataId, content);

            // 添加配置文件监听器
            nacosConfigManager.getConfigService().addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("国际化信息变更, dataId: {}, group:{}", dataId, group);
                    refreshConfig(dataId, configInfo);
                }
            });
        }
    }

    @SneakyThrows
    private synchronized void refreshConfig(String dataId, String content) {
        // i18n_result_code_zh-CN.properties
        // 清除本地缓存中的配置
        String beforeName = StrUtil.subBefore(dataId, dataIdExt, true);
        String language = StrUtil.subAfter(beforeName, StrUtil.UNDERLINE, true);
        String type = StrUtil.subBetween(beforeName, i18nProperties.getI18nStoragePrefix() + StrUtil.UNDERLINE, StrUtil.UNDERLINE + language);
        // key: result_code:zh-CN
        String key = StrUtil.join(StrPool.COLON, type, language);
        typeCodeMsgMap.remove(key);

        if (StrUtil.isBlank(content)) {
            return;
        }

        Properties properties = new Properties();
        properties.load(new StringReader(content));
        if (CollUtil.isEmpty(properties)) {
            return;
        }

        // 重新缓存指定 dataId 中的数据
        Map<String, String> codeMsgMap = new ConcurrentHashMap<>();
        properties.forEach((code, msg) -> {
            codeMsgMap.put(String.valueOf(code), String.valueOf(msg));
        });
        typeCodeMsgMap.put(key, codeMsgMap);
    }

}
