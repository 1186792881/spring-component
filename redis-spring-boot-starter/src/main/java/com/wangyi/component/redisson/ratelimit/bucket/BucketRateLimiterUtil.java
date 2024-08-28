package com.wangyi.component.redisson.ratelimit.bucket;

import cn.hutool.json.JSONUtil;
import com.wangyi.component.redisson.ratelimit.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@EnableScheduling
public class BucketRateLimiterUtil {

    private static final Map<String, RRateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BucketRateLimiterProperties bucketRateLimiterProperties;

    /**
     * @param key
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> T tryAcquire(String key, long rate, long rateInterval, long timeout, Supplier<T> supplier) {
        if (!bucketRateLimiterProperties.getEnabled()) {
            return supplier.get();
        }

        // 获取限流器, 并设置令牌生成速率
        BucketRateLimiterProperties.LimiterConfig limiterConfig = bucketRateLimiterProperties.getLimiterConfig(key);
        RRateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key, k -> {
            RRateLimiter limiter = redissonClient.getRateLimiter(bucketRateLimiterProperties.getKeyPrefix() + key);
            if (null == limiterConfig) {
                limiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.MILLISECONDS);
            } else {
                limiter.trySetRate(RateType.OVERALL, limiterConfig.getRate(), limiterConfig.getRateInterval(), RateIntervalUnit.MILLISECONDS);
            }
            return limiter;
        });

        // 每次获取1个令牌, 最多等待 timeout 毫秒
        timeout = (null == limiterConfig) ? timeout : limiterConfig.getTimeout();
        if (rateLimiter.tryAcquire(1, timeout, TimeUnit.MILLISECONDS)) {
            return supplier.get();
        }

        throw new RateLimitException("触发限流, key: " + key);
    }

    /**
     * 每分钟检查一次, 用于更新RateLimiter速率
     */
    @Scheduled(fixedDelay = 60_000)
    public void updateRateLimiter() {
        rateLimiterMap.forEach((key, rateLimiter) -> {
            BucketRateLimiterProperties.LimiterConfig limiterConfig = bucketRateLimiterProperties.getLimiterConfig(key);
            if (null != limiterConfig) {
                rateLimiter.setRate(RateType.OVERALL, limiterConfig.getRate(), limiterConfig.getRateInterval(), RateIntervalUnit.MILLISECONDS);
                log.info("更新限流速率完成, key: {},  limiterConfig: {}", key, JSONUtil.toJsonStr(limiterConfig));
            }
        });
    }

}
