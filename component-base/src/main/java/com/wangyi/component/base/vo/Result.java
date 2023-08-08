package com.wangyi.component.base.vo;

import com.wangyi.component.base.constant.LogConstant;
import com.wangyi.component.base.constant.ResultCode;
import org.slf4j.MDC;

public class Result<T> {

    private String code;

    private String msg;

    private T data;

    private String traceId;

    public Result(String code, String msg, T data) {
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

    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> fail(String code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
