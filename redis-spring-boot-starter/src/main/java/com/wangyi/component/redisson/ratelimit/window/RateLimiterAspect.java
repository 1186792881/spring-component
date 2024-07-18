package com.wangyi.component.redisson.ratelimit.window;

import com.wangyi.component.redisson.ratelimit.RateLimitException;
import com.wangyi.component.redisson.util.SpElUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class RateLimiterAspect {

    private static final String SEPARATOR = ":";

    @Resource
    private RateLimitUtil rateLimitUtil;

    @Pointcut("@annotation(com.wangyi.component.redisson.ratelimit.window.RateLimiter)")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (rateLimiter != null) {
            String keyPrefix = StringUtils.hasLength(rateLimiter.keyPrefix()) ? rateLimiter.keyPrefix() : SpElUtil.getMethodKey(method);
            String key = SpElUtil.parseSpEl(method, rateLimiter.key(), point.getArgs());
            // reidis中的限流key 为 keyPrefix + : + key
            String rateLimitKey = StringUtils.hasText(key) ? (keyPrefix + SEPARATOR + key) : keyPrefix;
            long max = rateLimiter.max();
            long window = rateLimiter.window();
            TimeUnit timeUnit = rateLimiter.timeUnit();
            boolean limited = rateLimitUtil.shouldLimited(rateLimitKey, max, window, timeUnit);
            if (limited) {
                throw new RateLimitException("触发限流, key: " + rateLimitKey);
            }
        }

        return point.proceed();
    }

}
