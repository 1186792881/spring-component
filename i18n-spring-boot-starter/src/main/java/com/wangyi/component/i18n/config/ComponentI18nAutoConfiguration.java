package com.wangyi.component.i18n.config;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.wangyi.component.i18n.source.I18nMessageSource;
import com.wangyi.component.i18n.source.RedisI18nMessageSource;
import com.wangyi.component.i18n.scan.ResultCodeScanRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

@ConditionalOnProperty(value = "i18n.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.wangyi.component.i18n")
@EnableSpringUtil
@Configuration
public class ComponentI18nAutoConfiguration {

    @Bean
    @Lazy
    @ConditionalOnMissingBean(I18nMessageSource.class)
    public I18nMessageSource i18nMessageSource(StringRedisTemplate stringRedisTemplate) {
        return new RedisI18nMessageSource(stringRedisTemplate);
    }

    @Bean
    @Lazy
    @ConditionalOnProperty(value = "i18n.enableScan", havingValue = "true", matchIfMissing = false)
    public ResultCodeScanRunner resultCodeScanRunner(I18nProperties i18nProperties, I18nMessageSource i18nMessageSource) {
        return new ResultCodeScanRunner(i18nProperties, i18nMessageSource);
    }

}
