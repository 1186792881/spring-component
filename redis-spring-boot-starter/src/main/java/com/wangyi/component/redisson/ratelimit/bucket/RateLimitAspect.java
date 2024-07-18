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
public class RateLimitAspect {

    @Resource
    private RateLimitUtil rateLimitUtil;

    @Around("@annotation(RateLimiter)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RateLimiter limit = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (null == limit) {
            return joinPoint.proceed();
        }

        return rateLimitUtil.tryAcquire(limit.key(), () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

    }
}