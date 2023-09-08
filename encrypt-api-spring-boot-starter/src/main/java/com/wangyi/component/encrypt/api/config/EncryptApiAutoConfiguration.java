package com.wangyi.component.encrypt.api.config;

import com.wangyi.component.encrypt.api.key.DefaultEncryptKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptKeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.wangyi.component.encrypt.api")
public class EncryptApiAutoConfiguration {

    /**
     * 默认的加解密密钥提供者, 通过配置文件去配置密钥
     * @param encryptProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(EncryptKeyProvider.class)
    public EncryptKeyProvider keyProvider(EncryptProperties encryptProperties) {
        return new DefaultEncryptKeyProvider(encryptProperties);
    }

}
