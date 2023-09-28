package com.wangyi.component.i18n.source;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.i18n.config.I18nProperties;
import com.wangyi.component.i18n.constant.LanguageEnum;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 从配置文件或配置中心获取国际化信息
 */
@ConditionalOnProperty(value = "i18n.storage", havingValue = "redis")
@Configuration
public class PropertiesI18nMessageSource implements I18nMessageSource {

    private final I18nProperties i18nProperties;
    private final String fileExt = ".properties";

    // key: result_code:zh-CN:example.00001,  value: param invalid
    private final Map<String, String> codeMsgMap = new ConcurrentHashMap<>();

    public PropertiesI18nMessageSource(I18nProperties i18nProperties) {
        this.i18nProperties = i18nProperties;
    }

    @Override
    public String getMessage(String type, String code, String language) {
        Map<String, String> map = getMessage(type, ListUtil.toList(code), language);
        return map.get(code);
    }

    @Override
    public Map<String, String> getMessage(String type, List<String> codeList, String language) {
        Map<String, String> map = new HashMap<>();

        if (CollUtil.isEmpty(codeList) || MapUtil.isEmpty(codeMsgMap)) {
            return map;
        }

        for (String code : codeList) {
            String key = StrUtil.join(StrPool.COLON, type, language, code);
            map.put(code, codeMsgMap.get(key));
        }

        return map;
    }

    @SneakyThrows
    @Override
    public void initMessage() {
        String dirPath = i18nProperties.getI18nStoragePrefix();
        List<String> fileNameList = FileUtil.listFileNames(dirPath);
        if (CollUtil.isEmpty(fileNameList)) {
            return;
        }

        // 筛选出 result_code_zh-CN.properties 格式的文件
        fileNameList = fileNameList.stream()
                .filter(fileName -> {
                    if (!fileName.endsWith(fileExt)) {
                        return false;
                    }
                    String beforeName = StrUtil.subBefore(fileName, fileExt, true);
                    if (StrUtil.isBlank(beforeName)) {
                        return false;
                    }
                    String language = StrUtil.subAfter(beforeName, StrUtil.UNDERLINE, true);
                    return LanguageEnum.contains(language);
                }).collect(Collectors.toList());

        if (CollUtil.isEmpty(fileNameList)) {
            return;
        }

        for (String fileName : fileNameList) {
            // 加载国际化配置
            String filePath = StrUtil.join(StrUtil.SLASH, dirPath, fileName);
            Properties properties = new Properties();
            properties.load(ResourceUtil.getStream(filePath));
            if (CollUtil.isEmpty(properties)) {
                continue;
            }

            // fileName: result_code_zh-CN.properties
            String beforeName = StrUtil.subBefore(fileName, fileExt, true);
            String type = StrUtil.subBefore(beforeName, StrUtil.UNDERLINE, true);
            String language = StrUtil.subAfter(beforeName, StrUtil.UNDERLINE, true);
            properties.forEach((code, msg) -> {
                // key: result_code:zh-CN:example.00001
                String key = StrUtil.join(StrPool.COLON, type, language, code);
                codeMsgMap.put(key, String.valueOf(msg));
            });
        }
    }

}
