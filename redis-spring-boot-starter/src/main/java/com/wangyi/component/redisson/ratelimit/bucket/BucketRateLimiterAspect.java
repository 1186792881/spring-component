package com.wangyi.component.redisson.ratelimit.bucket;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class BucketRateLimiterAspect {

    @Resource
    private BucketRateLimiterUtil bucketRateLimiterUtil;

    @Around("@annotation(com.wangyi.component.redisson.ratelimit.bucket.BucketRateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        BucketRateLimiter limit = AnnotationUtils.findAnnotation(method, BucketRateLimiter.class);
        if (null == limit) {
            return joinPoint.proceed();
        }

        return bucketRateLimiterUtil.tryAcquire(limit.key(), limit.rate(), limit.rateInterval(), limit.timeout(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

    }
}