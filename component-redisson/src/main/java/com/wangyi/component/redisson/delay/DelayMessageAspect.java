package com.wangyi.component.redisson.delay;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;

@Component
@Aspect
public class DelayMessageAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DelayMessageUtil delayMessageUtil;

    @Around("@annotation(com.wangyi.component.redisson.delay.DelayMessageListener)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DelayMessageListener listener = method.getAnnotation(DelayMessageListener.class);
        List<DelayMessage> delayMessageList = null;
        boolean check = false;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof DelayMessageEvent) {
                DelayMessageEvent event = (DelayMessageEvent) arg;
                delayMessageList = event.getDelayMessage();
                if (!CollectionUtils.isEmpty(delayMessageList)) {
                    check = listener.value().equals(delayMessageList.get(0).getTopic());
                    break;
                }
            }
        }

        // topic 匹配就执行
        if (check) {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                log.error("延迟消息消费失败, message: {}", delayMessageList, e);
                // 消费异常, 将消息重新推送到延迟队列中
                delayMessageList.forEach(msg -> {
                    try {
                        delayMessageUtil.retryPublish(msg, listener.retry());
                    } catch (Exception ex) {
                        log.error("延迟消息重试失败, message: {}", msg, ex);
                    }
                });
                throw e;
            }
        }

        return null;
    }


}
