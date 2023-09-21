package com.wangyi.component.base.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wangyi.component.base.constant.BaseConstant;
import com.wangyi.component.base.exception.BaseResultCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.MDC;

@Getter
@ToString
public class Result<T> {

    private final String code;

    private final String msg;

    private final T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object[] msgArgs;

    private final String traceId;

    private Result(String code, String msg, T data, Object... msgArgs) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (null != msgArgs && msgArgs.length > 0) {
            this.msgArgs = msgArgs;
        } else {
            this.msgArgs = null;
        }
        this.traceId = MDC.get(BaseConstant.TRACE_ID);
    }

    public static <T> Result<T> success() {
        return result(BaseResultCode.SUCCESS.getCode(), BaseResultCode.SUCCESS.getMsg(), null);
    }

    public static <T> Result<T> success(T t) {
        return result(BaseResultCode.SUCCESS.getCode(), BaseResultCode.SUCCESS.getMsg(), t);
    }

    public static <T> Result<T> fail(String code, String msg, Object... msgArgs) {
        return result(code, msg, null, msgArgs);
    }

    public static <T> Result<T> result(String code, String msg, T data, Object... msgArgs) {
        return new Result<>(code, msg, data, msgArgs);
    }

}
