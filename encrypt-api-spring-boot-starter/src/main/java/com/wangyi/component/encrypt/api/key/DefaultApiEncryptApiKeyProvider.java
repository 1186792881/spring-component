package com.wangyi.component.encrypt.api.key;

import com.wangyi.component.encrypt.api.config.EncryptApiProperties;
import com.wangyi.component.encrypt.api.enums.EncryptType;

import java.util.Map;

/**
 * 默认的密钥提供者, 通过配置文件配置密钥
 */
public class DefaultApiEncryptApiKeyProvider implements EncryptApiKeyProvider {

    private final EncryptApiProperties encryptApiProperties;

    public DefaultApiEncryptApiKeyProvider(EncryptApiProperties encryptApiProperties) {
        this.encryptApiProperties = encryptApiProperties;
    }

    @Override
    public EncryptKey getEncryptKey(EncryptType encryptType) {
        Map<String, EncryptKey> encryptKeyMap = encryptApiProperties.getEncryptKey();
        if (encryptKeyMap != null && encryptKeyMap.containsKey(encryptType.getValue())) {
            return encryptKeyMap.get(encryptType.getValue());
        }
        return null;
    }

}
