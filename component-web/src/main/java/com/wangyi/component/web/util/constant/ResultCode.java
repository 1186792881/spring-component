package com.wangyi.component.web.util.constant;

public enum ResultCode {

    SUCCESS(200, "SUCCESS"),
    FAIL(500, "FAIL"),
    INVALID_PARAMETER(505, "参数无效"),
    ;

    private final Integer code;
    private final String name;

    ResultCode(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
