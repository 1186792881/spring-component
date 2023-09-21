package com.wangyi.component.base.exception;

public enum BaseResultCode implements ResultCode {

    SUCCESS("200", "success"),
    INVALID_PARAMETER("400", "invalid parameter"),
    FAIL("500", "fail"),
    ;

    private final String code;
    private final String msg;

    BaseResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
