package com.wangyi.component.encrypt.api.config;

import com.wangyi.component.encrypt.api.key.EncryptKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties("encrypt.api")
@Component
@Data
public class EncryptApiProperties {

    /**
     * 是否启用接口加密
     */
    private Boolean enabled = true;

    /**
     * 密钥
     */
    private Map<String, EncryptKey> encryptKey;

}
