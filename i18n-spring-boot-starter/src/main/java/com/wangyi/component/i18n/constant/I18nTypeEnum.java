package com.wangyi.component.i18n.constant;

import lombok.Getter;

@Getter
public enum I18nTypeEnum {

    RESULT_CODE("result_code", "错误码"),
    FRONT_PAGE("front_page", "前端页面国际化");

    private final String value;
    private final String name;

    I18nTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
