package com.wangyi.component.example.constant;

import com.wangyi.component.base.exception.ResultCode;
import com.wangyi.component.i18n.annotation.I18nType;
import com.wangyi.component.i18n.constant.I18nTypeEnum;

@I18nType(I18nTypeEnum.RESULT_CODE)
public enum ExampleResultCode implements ResultCode {

    PARAM_INVALID("00001", "param {} invalid"),
    USER_NOT_EXISTS("00002", "user {} not exists");

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
