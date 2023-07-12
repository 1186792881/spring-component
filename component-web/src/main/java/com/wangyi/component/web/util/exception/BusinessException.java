package com.wangyi.component.web.util.exception;

import com.wangyi.component.web.util.constant.ResultCode;

/**
 * 自定义异常
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
