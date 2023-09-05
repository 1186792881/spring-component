package com.wangyi.component.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.base.constant.BaseConstant;
import com.wangyi.component.gateway.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class TraceIdGlobalFilter implements GlobalFilter, Ordered {

    private final RequestUtil requestUtil;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        setTraceId(exchange);
        setIp(exchange);
        printLog(exchange);
        return chain.filter(exchange).then(Mono.fromRunnable(this::clear));
    }

    /**
     * Header 设置 traceId
     * @param exchange
     */
    private void setTraceId(ServerWebExchange exchange) {
        // 请求头中设置 traceId
        String traceId = exchange.getRequest().getHeaders().getFirst(BaseConstant.TRACE_ID);
        if (!StringUtils.hasLength(traceId)) {
            traceId = UUID.randomUUID().toString().replaceAll("-", "");
        }
        exchange.getRequest().mutate().header(BaseConstant.TRACE_ID, traceId);
        MDC.put(BaseConstant.TRACE_ID, traceId);
    }

    /**
     * Header 设置 IP
     * @param exchange
     */
    private void setIp(ServerWebExchange exchange) {
        // 请求头中设置 ip
        String ip = requestUtil.getIpAddress(exchange.getRequest());
        exchange.getRequest().mutate().header(BaseConstant.X_REAL_IP, ip);
    }

    /**
     * 打印日志
     * @param exchange
     */
    @SneakyThrows
    private void printLog(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String header = objectMapper.writeValueAsString(request.getHeaders().toSingleValueMap());
        log.info("IP: {}, PATH: {}, METHOD: {}, HEADER: {}", requestUtil.getIpAddress(request), request.getPath(), request.getMethod(), header);
    }

    private void clear() {
        MDC.clear();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
