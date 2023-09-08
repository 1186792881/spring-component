package com.wangyi.component.encrypt.api.advice;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.wangyi.component.encrypt.api.annotation.Decrypt;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 请求数据的加密信息解密处理<br>
 * 本类只对控制器参数中含有<strong>{@link org.springframework.web.bind.annotation.RequestBody}</strong>
 * 以及package为<strong><code>com.wangyi.component.encrypt.api.annotation.Decrypt</code></strong>下的注解有效
 * </p>
 *
 * @see RequestBodyAdvice
 *
 */
@Order(1)
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    private final List<EncryptHandler> encryptHandlerList;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Decrypt decrypt = getDecrypt(methodParameter);
        if (Objects.isNull(decrypt)) {
            return false;
        }
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Decrypt decrypt = getDecrypt(parameter);
        if (null == decrypt) {
            return inputMessage;
        }

        // 获取http请求中原始的body
        String body = IoUtil.read(inputMessage.getBody(), StandardCharsets.UTF_8);
        log.info("加密请求: {}", body);

        // 解密
        if (!StrUtil.isEmpty(body)) {
            EncryptHandler encryptHandler = getEncryptHandler(decrypt.encryptType());
            EncryptBody encryptBody = new EncryptBody();
            encryptBody.setBody(body);
            encryptBody.setEncryptType(decrypt.encryptType());
            encryptBody.setEncryptKeyType(decrypt.encryptKeyType());
            body = encryptHandler.decrypt(encryptBody);
        }

        // 将解密之后的body数据重新封装为HttpInputMessage作为当前方法的返回值
        InputStream inputStream = IoUtil.toStream(body, StandardCharsets.UTF_8);

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return inputStream;
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    private boolean hasEncryptAnnotation(Method method) {
        return null != AnnotationUtils.findAnnotation(method, Decrypt.class);
    }

    private EncryptHandler getEncryptHandler(EncryptType encryptType) {
        EncryptHandler encryptHandler = encryptHandlerList.stream()
                .filter(handler -> handler.support(encryptType))
                .findFirst().orElse(null);
        Assert.notNull(encryptHandler, "未找到 " + encryptType.getValue() + " 加解密处理器");
        return encryptHandler;
    }

    private Decrypt getDecrypt(MethodParameter parameter) {
        Method method = parameter.getMethod();
        Class<?> clazz = parameter.getDeclaringClass();
        Decrypt decrypt = null;
        if (null != method) {
            decrypt = AnnotationUtils.findAnnotation(method, Decrypt.class);
        }
        if (null == decrypt) {
            decrypt = AnnotationUtils.findAnnotation(clazz, Decrypt.class);
        }
        return decrypt;
    }

}