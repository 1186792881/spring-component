package com.wangyi.component.redisson.ratelimit.bucket;

import com.wangyi.component.redisson.ratelimit.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
public class RateLimitUtil {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RateLimitProperties rateLimitProperties;

    /**
     * @param key
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T tryAcquire(String key, Supplier<T> supplier) {
        if (!rateLimitProperties.getEnabled()) {
            return supplier.get();
        }

        // 获取限流器, 并设置令牌生成速率
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(rateLimitProperties.getKeyPrefix() + key);
        RateLimitProperties.LimiterConfig limiterConfig = rateLimitProperties.getLimiterConfig(key);
        rateLimiter.trySetRate(RateType.OVERALL, limiterConfig.getRate(), limiterConfig.getRateInterval(), RateIntervalUnit.SECONDS);

        // 每次获取1个令牌, 最多等待 limiterConfig.getTimeout() 毫秒
        if (rateLimiter.tryAcquire(1, limiterConfig.getTimeout(), TimeUnit.MILLISECONDS)) {
            return supplier.get();
        }

        throw new RateLimitException("触发限流, key: " + key);
    }

}
