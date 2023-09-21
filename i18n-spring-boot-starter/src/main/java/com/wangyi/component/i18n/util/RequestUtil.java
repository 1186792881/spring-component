package com.wangyi.component.i18n.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.wangyi.component.base.constant.BaseConstant;
import com.wangyi.component.i18n.constant.LanguageEnum;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestUtil {

    /**
     * 获取当前线程中的 HttpServletRequest
     * @return
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
    }

    /**
     * 获取当前线程中的 HttpServletRequest
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
    }

    /**
     * 获取请求头
     * @param headerName
     * @return
     */
    public static String getHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    /**
     * 获取客户端IP
     * @return
     */
    public static String getClientIp() {
        return ServletUtil.getClientIP(getRequest(), BaseConstant.X_REAL_IP);
    }

    /**
     * 获取客户端语言
     * @return
     */
    public static String getLanguage() {
        String language = getHeader(BaseConstant.LANGUAGE);
        if (StrUtil.isBlank(language)) {
            return LanguageEnum.CHINESE_CHINA.getValue();
        }
        return language;
    }

}
