package com.wangyi.component.redisson.ratelimit.bucket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 限流配置
 */
@ConfigurationProperties("rate-limit")
@Component
@Data
public class RateLimitProperties {

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 限流器key前缀
     */
    private String keyPrefix = "rate-limt:";

    /**
     * 每个限流器可以自定义配置
     * key -> 限流器key, value-> 限流器的配置
     */
    private Map<String, LimiterConfig> limiter;

    @Data
    static class LimiterConfig {
        /**
         * 每隔 rateInterval 增加 20 个令牌桶
         */
        private Long rate = 20L;

        /**
         * 往令牌桶中添加令牌的时间间隔
         * 默认 1 秒
         */
        private Long rateInterval = 1L;

        /**
         * 获取令牌最大等待时间
         */
        private Long timeout = 0L;
    }

    /**
     * 获取限流器配置
     * @return
     */
    public LimiterConfig getLimiterConfig(String key) {
        if (null == limiter || !limiter.containsKey(key)) {
            return new LimiterConfig();
        }
        return limiter.get(key);
    }

}
