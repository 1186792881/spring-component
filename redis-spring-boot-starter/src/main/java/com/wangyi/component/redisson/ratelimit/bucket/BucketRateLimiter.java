package com.wangyi.component.redisson.ratelimit.bucket;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface BucketRateLimiter {

    /**
     * 限流key
     *
     * @return
     */
    String key();

    /**
     * 限流速率
     * 每隔 rateInterval 增加 20 个令牌
     * @return
     */
    long rate() default 20L;

    /**
     * 限流速率间隔
     * 默认 1000 毫秒
     * @return
     */
    long rateInterval() default 1000L;

    /**
     * 获取令牌最大等待时间
     * 单位毫秒
     * @return
     */
    long timeout() default  0L;

}