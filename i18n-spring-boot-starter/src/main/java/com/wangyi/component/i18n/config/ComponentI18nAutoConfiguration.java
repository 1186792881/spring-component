package com.wangyi.component.i18n.config;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.wangyi.component.i18n.source.I18nMessageInitRunner;
import com.wangyi.component.i18n.source.I18nMessageSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@ConditionalOnProperty(value = "i18n.enable", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.wangyi.component.i18n")
@EnableSpringUtil
@Configuration
public class ComponentI18nAutoConfiguration {

    @Bean
    @Lazy
    public I18nMessageInitRunner i18nMessageInitRunner(I18nMessageSource i18nMessageSource) {
        return new I18nMessageInitRunner(i18nMessageSource);
    }

}
