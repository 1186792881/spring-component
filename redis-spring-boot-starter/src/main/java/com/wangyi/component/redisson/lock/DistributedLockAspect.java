package com.wangyi.component.redisson.lock;

import com.wangyi.component.redisson.util.SpElUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Order(Integer.MIN_VALUE + 1)
@Aspect
@Component
public class DistributedLockAspect {

    @Resource
    private DistributedLockUtil distributedLockUtil;

    /**
     * 带有 DistributedLock 注解的方法会添加分布式锁
     */
    @Pointcut(
            "@annotation(com.wangyi.component.redisson.lock.DistributedLock)"
    )
    public void lockPointcut() {
        // 方法为空，因为这只是一个切入点，实现在通知中。
    }

    /**
     * 在方法进入和退出时记录日志的通知
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("lockPointcut()")
    public Object lockAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String keyPrefix = StringUtils.hasLength(distributedLock.keyPrefix()) ? distributedLock.keyPrefix() : SpElUtil.getMethodKey(method);
        String key = SpElUtil.parseSpEl(method, distributedLock.key(), joinPoint.getArgs());
        int waitTime = distributedLock.waitTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        return distributedLockUtil.tryLockWithThrows(keyPrefix + ":" + key, waitTime, timeUnit, joinPoint::proceed);
    }

}
