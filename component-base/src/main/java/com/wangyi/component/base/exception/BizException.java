package com.wangyi.component.base.exception;

import com.wangyi.component.base.constant.ResultCode;

/**
 * 自定义异常
 */
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;
    private String message;

    public BizException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
