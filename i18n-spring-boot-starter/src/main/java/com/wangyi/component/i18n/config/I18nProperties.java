package com.wangyi.component.i18n.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("i18n")
@Component
@Data
public class I18nProperties {

    /**
     * 是否启用国际化组件
     */
    private boolean enable = true;

    /**
     * 是否在服务启动时, 初始化国际化信息
     */
    private boolean enableInit = true;

    /**
     * 国际化信息保存方式
     */
    private String storage;

    /**
     * 存储前缀
     * redis:               i18n:result_code:zh-CN
     * properties:          i18n/result_code_zh-CN.properties
     */
    private String i18nStoragePrefix = "i18n";

    /**
     * 要扫描的包
     */
    private String scanPackage;

    /**
     * nacos中配置国际化信息的 group
     */
    private String nacosI18nGroup = "DEFAULT_GROUP";

    /**
     * nacos中配置国际化信息的 dataId
     * 如: i18n_result_code_zh-CN.properties, i18n_front_page_zh-CN.properties
     */
    private List<String> nacosI18nDataIdList = new ArrayList<>();


}
