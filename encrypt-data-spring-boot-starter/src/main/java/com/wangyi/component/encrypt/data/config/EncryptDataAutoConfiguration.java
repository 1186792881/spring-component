package com.wangyi.component.encrypt.data.config;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.wangyi.component.encrypt.data.core.MybatisDecryptionPlugin;
import com.wangyi.component.encrypt.data.core.MybatisEncryptionPlugin;
import com.wangyi.component.encrypt.data.key.DefaultEncryptDataKeyProvider;
import com.wangyi.component.encrypt.data.key.EncryptDataKeyProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "encrypt.data.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan("com.wangyi.component.encrypt.data")
@EnableSpringUtil
public class EncryptDataAutoConfiguration {

    /**
     * 默认的加解密密钥提供者, 通过配置文件去配置密钥
     * @param encryptDataProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(EncryptDataKeyProvider.class)
    public EncryptDataKeyProvider encryptDataKeyProvider(EncryptDataProperties encryptDataProperties) {
        return new DefaultEncryptDataKeyProvider(encryptDataProperties);
    }

    @Bean
    @ConditionalOnMissingBean(MybatisEncryptionPlugin.class)
    public MybatisEncryptionPlugin encryptionInterceptor(EncryptDataProperties properties) {
        return new MybatisEncryptionPlugin(properties);
    }

    @Bean
    @ConditionalOnMissingBean(MybatisDecryptionPlugin.class)
    public MybatisDecryptionPlugin decryptionInterceptor(EncryptDataProperties properties) {
        return new MybatisDecryptionPlugin(properties);
    }

}
