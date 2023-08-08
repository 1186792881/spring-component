package com.wangyi.component.web.aop;

import com.wangyi.component.base.constant.LogConstant;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * MDC 过滤器, 设置 traceId
 */
@Order(Integer.MIN_VALUE)
@WebFilter(filterName = "mdcFilter", urlPatterns = {"/*"})
public class MdcFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 设置traceId
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String traceId = request.getHeader(LogConstant.TRACE_ID);
        if (!StringUtils.hasLength(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        MDC.put(LogConstant.TRACE_ID, traceId);
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }

}
