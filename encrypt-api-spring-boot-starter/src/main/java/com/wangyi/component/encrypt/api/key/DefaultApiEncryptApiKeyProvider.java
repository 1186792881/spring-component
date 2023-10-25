package com.wangyi.component.encrypt.api.key;

import com.wangyi.component.encrypt.api.config.EncryptApiProperties;

/**
 * 默认的密钥提供者, 通过配置文件配置密钥
 */
public class DefaultApiEncryptApiKeyProvider implements EncryptApiKeyProvider {

    private final EncryptApiProperties encryptApiProperties;

    public DefaultApiEncryptApiKeyProvider(EncryptApiProperties encryptApiProperties) {
        this.encryptApiProperties = encryptApiProperties;
    }

    @Override
    public EncryptKey getKey() {
        EncryptKey encryptKey = new EncryptKey();
        encryptKey.setAesKey(encryptApiProperties.getAes());
        encryptKey.setDesKey(encryptApiProperties.getDes());
        encryptKey.setRsaKey(encryptApiProperties.getRsa());
        encryptKey.setSm2Key(encryptApiProperties.getSm2());
        encryptKey.setSm4Key(encryptApiProperties.getSm4());
        return encryptKey;
    }

}
