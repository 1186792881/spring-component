package com.wangyi.component.encrypt.api.config;

import com.wangyi.component.encrypt.api.key.DefaultApiEncryptApiKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "encrypt.api.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.wangyi.component.encrypt.api")
public class EncryptApiAutoConfiguration {

    /**
     * 默认的加解密密钥提供者, 通过配置文件去配置密钥
     * @param encryptApiProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(EncryptApiKeyProvider.class)
    public EncryptApiKeyProvider encryptApiKeyProvider(EncryptApiProperties encryptApiProperties) {
        return new DefaultApiEncryptApiKeyProvider(encryptApiProperties);
    }

}
