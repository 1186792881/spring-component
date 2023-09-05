package com.wangyi.component.gateway.predicate;

import com.wangyi.component.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 缓存 requestBody
 * 在拦截器中使用 exchange.getAttribute(GatewayConstant.REQUEST_BODY_CACHE) 获取请求体
 * https://mp.weixin.qq.com/s/BuIeQl4hqQiHXiY5tZsLqQ
 */
@Slf4j
//@Component
public class ReadBodyRoutePredicateFactory extends AbstractRoutePredicateFactory<ReadBodyRoutePredicateFactory.Config> {

    private List<HttpMessageReader<?>> messageReaders;

    @Value("${spring.codec.max-in-memory-size:10MB}")
    private DataSize maxInMemory;

    public ReadBodyRoutePredicateFactory() {
        super(Config.class);
        this.messageReaders = HandlerStrategies.withDefaults().messageReaders();
    }

    public ReadBodyRoutePredicateFactory(List<HttpMessageReader<?>> messageReaders) {
        super(Config.class);
        this.messageReaders = messageReaders;
    }

    @PostConstruct
    private void overrideMsgReaders() {
        this.messageReaders = HandlerStrategies.builder()
                .codecs((c) -> c.defaultCodecs().maxInMemorySize((int) maxInMemory.toBytes()))
                .build()
                .messageReaders();
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(Config config) {
        return new AsyncPredicate<ServerWebExchange>() {
            @Override
            public Publisher<Boolean> apply(ServerWebExchange exchange) {
                Class inClass = config.getInClass();
                Object cachedBody = exchange.getAttribute(GatewayConstant.REQUEST_BODY_CACHE);
                if (cachedBody != null) {
                    try {
                        boolean test = config.predicate.test(cachedBody);
                        //exchange.getAttributes().put("read_body_predicate_test_attribute", test);
                        return Mono.just(test);
                    } catch (ClassCastException var6) {
                        log.warn("Predicate test failed because class in predicate does not match the cached body object", var6);
                        return Mono.just(false);
                    }
                } else {
                    return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange, (request) -> {
                        return ServerRequest.create(exchange.mutate().request(request).build(), ReadBodyRoutePredicateFactory.this.messageReaders)
                                .bodyToMono(inClass)
                                .doOnNext((objectValue) -> {exchange.getAttributes().put(GatewayConstant.REQUEST_BODY_CACHE, objectValue);})
                                .map((objectValue) -> {return config.getPredicate().test(objectValue);})
                                .thenReturn(true);
                    });
                }
            }

            @Override
            public String toString() {
                return String.format("ReadBody: %s", config.getInClass());
            }
        };
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        throw new UnsupportedOperationException("ReadBodyPredicateFactory is only async.");
    }

    public static class Config {
        private Class inClass;
        private Predicate predicate;
        private Map<String, Object> hints;

        public Config() {
        }

        public Class getInClass() {
            return this.inClass;
        }

        public Config setInClass(Class inClass) {
            this.inClass = inClass;
            return this;
        }

        public Predicate getPredicate() {
            return this.predicate;
        }

        public Config setPredicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public <T> Config setPredicate(Class<T> inClass, Predicate<T> predicate) {
            this.setInClass(inClass);
            this.predicate = predicate;
            return this;
        }

        public Map<String, Object> getHints() {
            return this.hints;
        }

        public Config setHints(Map<String, Object> hints) {
            this.hints = hints;
            return this;
        }
    }
}
