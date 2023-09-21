package com.wangyi.component.i18n.core;

import com.wangyi.component.i18n.constant.I18nConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(RequestInterceptor.class)
@Configuration
public class I18nFeignInterceptor implements RequestInterceptor {

    /**
     * 通过 feign 拦截器在 header 中设置标记, 判断是否为服务间调用
     * 服务间调用时返回结果, 不需要对Result.msg进行国际化
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(I18nConstant.FEIGN_I18N_HEADER, I18nConstant.FEIGN_I18N_VALUE);
    }

}
