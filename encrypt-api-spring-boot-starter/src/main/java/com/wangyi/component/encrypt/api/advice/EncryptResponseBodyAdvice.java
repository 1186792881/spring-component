package com.wangyi.component.encrypt.api.advice;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangyi.component.base.vo.Result;
import com.wangyi.component.encrypt.api.annotation.Encrypt;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.List;
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

    private final List<EncryptHandler> encryptHandlerList;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Encrypt encrypt = getEncrypt(returnType);
        if (Objects.isNull(encrypt)) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public Result beforeBodyWrite(Result body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Encrypt encrypt = getEncrypt(returnType);
        if (null == encrypt) {
            return body;
        }

        if (Objects.isNull(body) || Objects.isNull(body.getData())) {
            return body;
        }

        String plainData = objectMapper.writeValueAsString(body.getData());
        if (StrUtil.isEmpty(plainData)) {
            return body;
        }

        EncryptHandler encryptHandler = getEncryptHandler(encrypt.encryptType());
        EncryptBody encryptBody = new EncryptBody();
        encryptBody.setBody(plainData);
        encryptBody.setEncryptType(encrypt.encryptType());
        encryptBody.setEncryptKeyType(encrypt.encryptKeyType());
        String encryptData = encryptHandler.encrypt(encryptBody);
        body.setData(encryptData);
        log.info("加密响应: {}", objectMapper.writeValueAsString(body));
        return body;
    }

    private EncryptHandler getEncryptHandler(EncryptType encryptType) {
        EncryptHandler encryptHandler = encryptHandlerList.stream()
                .filter(handler -> handler.support(encryptType))
                .findFirst().orElse(null);
        Assert.notNull(encryptHandler, "未找到 " + encryptType.getValue() + " 加解密处理器");
        return encryptHandler;
    }

    private Encrypt getEncrypt(MethodParameter returnType) {
        Method method = returnType.getMethod();
        Class<?> clazz = returnType.getDeclaringClass();
        Encrypt encrypt = null;
        if (null != method) {
            encrypt = AnnotationUtils.findAnnotation(method, Encrypt.class);
        }
        if (null == encrypt) {
            encrypt = AnnotationUtils.findAnnotation(clazz, Encrypt.class);
        }
        return encrypt;
    }

}
