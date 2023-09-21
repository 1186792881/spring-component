package com.wangyi.component.base.exception;


/**
 * 业务断言, 抛出 BizException, 最终进入 GlobalExceptionHandler.handleBizException()
 */
public class BizAssert {

    public static void isNull(Object obj, ResultCode resultCode, Object... msgArgs) {
        isTrue(null == obj, resultCode.getCode(), resultCode.getMsg(), msgArgs);
    }

    public static void isNull(Object obj, String code, String msg, Object... msgArgs) {
        isTrue(null == obj, code, msg, msgArgs);
    }

    public static void isNotNull(Object obj, ResultCode resultCode, Object... msgArgs) {
        isTrue(null != obj, resultCode.getCode(), resultCode.getMsg(), msgArgs);
    }

    public static void isNotNull(Object obj, String code, String msg, Object... msgArgs) {
        isTrue(null != obj, code, msg, msgArgs);
    }

    public static void isFalse(boolean flag, ResultCode resultCode, Object... msgArgs) {
        isTrue(!flag, resultCode.getCode(), resultCode.getMsg(), msgArgs);
    }

    public static void isFalse(boolean flag, String code, String msg, Object... msgArgs) {
        isTrue(!flag, code, msg, msgArgs);
    }

    public static void isTrue(boolean flag, ResultCode resultCode, Object... msgArgs) {
        isTrue(flag, resultCode.getCode(), resultCode.getMsg(), msgArgs);
    }

    public static void isTrue(boolean flag, String code, String msg, Object... msgArgs) {
        if (flag) {
            throw new BizException(code, msg, null, msgArgs);
        }
    }

}
