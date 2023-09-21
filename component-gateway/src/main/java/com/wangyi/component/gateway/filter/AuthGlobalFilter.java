package com.wangyi.component.gateway.filter;

import com.wangyi.component.base.exception.BaseResultCode;
import com.wangyi.component.base.vo.Result;
import com.wangyi.component.gateway.config.AuthConfig;
import com.wangyi.component.gateway.util.RequestUtil;
import com.wangyi.component.gateway.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 接口权限校验
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthConfig authConfig;
    private final ResponseUtil responseUtil;
    private final RequestUtil requestUtil;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 如果未启用网关验证，则跳过
        if (!authConfig.isEnable()) {
            return chain.filter(exchange);
        }

        // 匹配上的路径无需校验, 直接放行
        String path = exchange.getRequest().getPath().toString();
        for (String ignorePath : authConfig.getIgnorePath()) {
            if (antPathMatcher.match(ignorePath, path)) {
                return chain.filter(exchange);
            }
        }

        // 校验token是否有权限访问指定路径
        String token = requestUtil.getToken(exchange.getRequest());
        Result<Void> result = validateToken(token, path);
        if (!BaseResultCode.SUCCESS.getCode().equals(result.getCode())) {
            return responseUtil.webFluxResponseWriter(exchange.getResponse(), result);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 校验token是否有权限访问指定路径
     * @param token
     * @param path
     * @return
     */
    private Result<Void> validateToken(String token, String path) {
        // TODO 验证token 是否有效
        if (!StringUtils.hasText(token)) {
            return Result.fail(String.valueOf(HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }

        // TODO 验证token 是否有权限
        if (!token.contains(path)) {
            return Result.fail(String.valueOf(HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN.getReasonPhrase());
        }

        return Result.success();
    }

}
