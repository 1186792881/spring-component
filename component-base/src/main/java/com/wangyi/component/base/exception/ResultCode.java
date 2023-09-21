package com.wangyi.component.base.exception;

public interface ResultCode {

    /**
     * 获取错误码
     * @return
     */
    String getCode();

    /**
     * 获取错误消息
     * @return
     */
    String getMsg();
}
