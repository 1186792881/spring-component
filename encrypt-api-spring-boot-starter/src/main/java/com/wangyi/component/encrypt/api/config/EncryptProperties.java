package com.wangyi.component.encrypt.api.config;

import com.wangyi.component.encrypt.api.handler.aes.AesKey;
import com.wangyi.component.encrypt.api.handler.des.DesKey;
import com.wangyi.component.encrypt.api.handler.rsa.RsaKey;
import com.wangyi.component.encrypt.api.handler.sm2.Sm2Key;
import com.wangyi.component.encrypt.api.handler.sm4.Sm4Key;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("encrypt.api")
@Component
@Data
public class EncryptProperties {

    /**
     * AES 密钥配置
     */
    private AesKey aes;

    /**
     * DES 密钥配置
     */
    private DesKey des;

    /**
     * RSA 密钥配置
     */
    private RsaKey rsa;

    /**
     * SM2 密钥配置
     */
    private Sm2Key sm2;

    /**
     * SM4 密钥配置
     */
    private Sm4Key sm4;

}
