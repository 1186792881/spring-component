package com.wangyi.component.gateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.base.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ResponseUtil {

    private final ObjectMapper objectMapper;

    /**
     * 设置webflux模型响应
     *
     * @param response    ServerHttpResponse
     * @param result      响应内容
     * @return Mono<Void>
     */
    @SneakyThrows
    public Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Result result) {
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        DataBuffer dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(result));
        return response.writeWith(Mono.just(dataBuffer));
    }

}
