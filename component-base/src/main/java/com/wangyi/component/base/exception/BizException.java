package com.wangyi.component.base.exception;

import lombok.ToString;

/**
 * 自定义异常
 */
@ToString
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
    private Object data;
    private Object[] msgArgs;

    BizException(String code, String message, Object data, Object... messageArgs) {
        super(message);
        this.code = code;
        this.msg = message;
        this.data = data;
        this.msgArgs = messageArgs;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public Object[] getMsgArgs() {
        return msgArgs;
    }
}
