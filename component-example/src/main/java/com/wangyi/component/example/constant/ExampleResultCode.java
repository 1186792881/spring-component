package com.wangyi.component.example.constant;

import com.wangyi.component.base.exception.ResultCode;

public enum ExampleResultCode implements ResultCode {

    PARAM_INVALID("00001", "param {} invalid");

    private final String code;
    private final String msg;

    ExampleResultCode(String code, String msg) {
        this.code = "example." + code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
