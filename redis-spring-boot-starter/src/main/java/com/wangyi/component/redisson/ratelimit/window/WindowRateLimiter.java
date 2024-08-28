package com.wangyi.component.redisson.ratelimit.window;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WindowRateLimiter {

    /**
     * max 最大请求数
     */
    long max();

    /**
     * key前缀, 默认取方法全限定名, 除非在不同方法上对同一个资源限流, 就自己指定
     * @return
     */
    String keyPrefix() default "";

    /**
     * 限流key, 支持spel表达式
     */
    String key() default "";

    /**
     * 时间窗口，默认1分钟
     */
    long window() default 1;

    /**
     * 时间窗口单位，默认 分钟
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

}
