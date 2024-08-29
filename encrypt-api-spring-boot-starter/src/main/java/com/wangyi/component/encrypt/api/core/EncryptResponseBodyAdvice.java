package com.wangyi.component.encrypt.api.core;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.base.vo.Result;
import com.wangyi.component.encrypt.api.annotation.EncryptResponseBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.handler.EncryptHandlerHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 响应数据的加密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.ResponseBody}</strong>
 * 或者控制类上含有<strong>{@link org.springframework.web.bind.annotation.RestController}</strong>
 * 以及package为<strong><code>com.wangyi.component.encrypt.api.annotation.Decrypt</code></strong>下的注解有效
 *
 * @see ResponseBodyAdvice
 */
@Order(1)
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Result> {

    private final EncryptHandlerHolder encryptHandlerHolder;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        EncryptResponseBody encryptResponseBody = getEncrypt(returnType);
        if (Objects.isNull(encryptResponseBody)) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public Result beforeBodyWrite(Result body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        EncryptResponseBody encryptResponseBody = getEncrypt(returnType);
        if (null == encryptResponseBody) {
            return body;
        }

        if (Objects.isNull(body) || Objects.isNull(body.getData())) {
            return body;
        }

        String plainData = objectMapper.writeValueAsString(body.getData());
        if (StrUtil.isEmpty(plainData)) {
            return body;
        }

        EncryptHandler encryptHandler = encryptHandlerHolder.getEncryptHandler(encryptResponseBody.encryptType());
        String encryptData = encryptHandler.encrypt(plainData);
        body = Result.result(body.getCode(), body.getMsg(), encryptData, body.getMsgArgs());
        log.info("加密响应: {}", objectMapper.writeValueAsString(body));
        return body;
    }

    private EncryptResponseBody getEncrypt(MethodParameter returnType) {
        Method method = returnType.getMethod();
        Class<?> clazz = returnType.getDeclaringClass();
        EncryptResponseBody encryptResponseBody = null;
        if (null != method) {
            encryptResponseBody = AnnotationUtils.findAnnotation(method, EncryptResponseBody.class);
        }
        if (null == encryptResponseBody) {
            encryptResponseBody = AnnotationUtils.findAnnotation(clazz, EncryptResponseBody.class);
        }
        return encryptResponseBody;
    }

}
