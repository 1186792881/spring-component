package com.wangyi.component.i18n.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.wangyi.component.base.vo.Result;
import com.wangyi.component.i18n.constant.I18nConstant;
import com.wangyi.component.i18n.constant.I18nTypeEnum;
import com.wangyi.component.i18n.source.I18nMessageSource;
import com.wangyi.component.i18n.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 对响应 Result 中的 msg 做国际化
 * @see ResponseBodyAdvice
 */
@Order(2)
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class I18nResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof Result)) {
            return body;
        }

        Result result = (Result) body;
        String code = result.getCode();
        String language = RequestUtil.getLanguage();
        String msg = result.getMsg();

        // 如果是微服务间的调用, 则不需要国际化, 由最终的前端调用响应时再做国际化
        // 通过 feign 拦截器在 header 中设置标记, 判断是否为服务间调用
        String feignFlag = RequestUtil.getHeader(I18nConstant.FEIGN_I18N_HEADER);
        if (StrUtil.equals(feignFlag, I18nConstant.FEIGN_I18N_VALUE)) {
            return result;
        }

        // 如果不是微服务间的调用, 则根据 code 和 language 查询国际化信息
        ObjectProvider<I18nMessageSource> messageSourceProvider = SpringUtil.getApplicationContext().getBeanProvider(I18nMessageSource.class);
        I18nMessageSource i18nMessageSource = messageSourceProvider.getIfAvailable();
        if (null != i18nMessageSource) {
            String i18nMsg = i18nMessageSource.getMessage(I18nTypeEnum.RESULT_CODE.getValue(), language, code);
            if (StrUtil.isNotBlank(i18nMsg)) {
                msg = i18nMsg;
            }
        }

        // 为 msg 填充参数
        if (StrUtil.isNotBlank(msg) && null != result.getMsgArgs()) {
            msg = StrUtil.format(msg, result.getMsgArgs());
        }

        // 响应中去掉 msgArgs
        return Result.result(result.getCode(), msg, result.getData());
    }

}
