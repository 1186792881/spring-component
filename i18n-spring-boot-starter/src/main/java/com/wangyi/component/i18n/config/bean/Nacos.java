package com.wangyi.component.i18n.config.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Nacos {

    /**
     * nacos中配置国际化信息的 group
     */
    private String i18nGroup = "DEFAULT_GROUP";

    /**
     * nacos中配置国际化信息的 dataId
     * 如: i18n_result_code_zh-CN.properties, i18n_front_page_zh-CN.properties
     */
    private List<String> i18nDataIdList = new ArrayList<>();
}
