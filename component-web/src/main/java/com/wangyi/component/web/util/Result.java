package com.wangyi.component.web.util;

import com.wangyi.component.web.util.constant.LogConstant;
import com.wangyi.component.web.util.constant.ResultCode;
import org.slf4j.MDC;

public class Result<T> {

    private Integer code;

    private String msg;

    private T data;

    private String traceId;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.traceId = MDC.get(LogConstant.TRACE_ID);
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.name(), t);
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.name(), null);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL.getCode(), ResultCode.FAIL.name(), null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCode.FAIL.getCode(), msg, null);
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
