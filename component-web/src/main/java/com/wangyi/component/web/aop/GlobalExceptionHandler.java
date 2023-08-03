package com.wangyi.component.web.aop;

import com.wangyi.component.web.util.Result;
import com.wangyi.component.web.util.constant.ResultCode;
import com.wangyi.component.web.util.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBizException(BusinessException ex) {
        log.error(ex.getMessage(), ex);
        return Result.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 参数校验不通过异常
     * 将请求体解析并绑定到 java bean 时，如果出错，则抛出 MethodArgumentNotValidException 异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("参数错误", ex);
        if (!ex.getBindingResult().hasErrors()) {
            return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName());
        }
        Map<String, String> map = new HashMap<>();
        for (ObjectError objectError : ex.getAllErrors()) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                map.put(objectError.getObjectName(), objectError.getDefaultMessage());
            }
        }
        return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName(), map);
    }

    /**
     * 参数校验不通过异常
     * 表单绑定到 java bean 出错时，会抛出 BindException 异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException ex) {
        log.error("参数错误", ex);
        if (!ex.getBindingResult().hasErrors()) {
            return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName());
        }
        Map<String, String> map = new HashMap<>();
        for (ObjectError objectError : ex.getAllErrors()) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                map.put(objectError.getObjectName(), objectError.getDefaultMessage());
            }
        }
        return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName(), map);
    }

    /**
     * 普通参数(非 java bean)校验出错时，会抛出 ConstraintViolationException 异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("参数错误", ex);
        Set<ConstraintViolation<?>> violationSet = ex.getConstraintViolations();
        if (null == violationSet || violationSet.isEmpty()) {
            return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName());
        }

        Map<String, String> map = new HashMap<>();
        violationSet.forEach(e -> {
            String field = e.getPropertyPath().toString().split("\\.")[1];
            map.put(field, e.getMessage());
        });
        return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName(), map);
    }

    /**
     * Controller参数绑定错误
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("参数错误", ex);
        return Result.fail(ResultCode.INVALID_PARAMETER.getCode(), ResultCode.INVALID_PARAMETER.getName());
    }

    /**
     * 其他未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<Void> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return Result.fail(ResultCode.FAIL.getCode(), ResultCode.FAIL.getName());
    }

}
