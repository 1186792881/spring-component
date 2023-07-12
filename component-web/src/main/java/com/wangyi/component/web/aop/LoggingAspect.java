package com.wangyi.component.web.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 打印请求日志
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 匹配接口的切入点
     */
    @Pointcut(
            "@annotation(org.springframework.web.bind.annotation.GetMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)"
    )
    public void apiPointcut() {
        // 方法为空，因为这只是一个切入点，实现在通知中。
    }

    /**
     * 记录方法抛出异常的通知
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "apiPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
    }

    /**
     * 在方法进入和退出时记录日志的通知
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("apiPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String params = objectMapper.writeValueAsString(filterArgs(joinPoint.getArgs()));
        log.info("Enter: {}.{}() with params = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), params);
        try {
            Object result = joinPoint.proceed();
            log.info("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), objectMapper.writeValueAsString(result));
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", params,
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }

    }

    private Object filterArgs(Object[] objects) {
        List<Object> args = Arrays.stream(objects).filter(obj -> !(obj instanceof MultipartFile)
                        && !(obj instanceof HttpServletResponse)
                        && !(obj instanceof HttpServletRequest))
                .collect(Collectors.toList());
        return args.size() == 1 ? args.get(0) : args;
    }

}