package com.wangyi.component.encrypt.data.config;

import com.wangyi.component.encrypt.data.handler.aes.AesKey;
import com.wangyi.component.encrypt.data.handler.des.DesKey;
import com.wangyi.component.encrypt.data.handler.rsa.RsaKey;
import com.wangyi.component.encrypt.data.handler.sm2.Sm2Key;
import com.wangyi.component.encrypt.data.handler.sm4.Sm4Key;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("encrypt.data")
@Component
@Data
public class EncryptDataProperties {

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

    private boolean enabled = true;

    /**
     * 快速失败，加解密过程中发生异常是否中断。true:抛出异常，false:使用原始值，打印 warn 级别日志
     */
    private boolean failFast = true;

    /**
     * 是否保存原参数不变
     */
    private boolean keepParameter = true;

}
