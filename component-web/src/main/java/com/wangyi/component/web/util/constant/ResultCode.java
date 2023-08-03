package com.wangyi.component.web.util.constant;

public enum ResultCode {

    SUCCESS("200", "SUCCESS"),
    INVALID_PARAMETER("400", "参数无效"),
    FAIL("500", "FAIL"),
    ;

    private final String code;
    private final String name;

    ResultCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
