package com.wangyi.component.encrypt.data.key;

import com.wangyi.component.encrypt.data.config.EncryptDataProperties;

/**
 * 默认的密钥提供者, 通过配置文件配置密钥
 */
public class DefaultEncryptDataKeyProvider implements EncryptDataKeyProvider {

    private final EncryptDataProperties encryptDataProperties;

    public DefaultEncryptDataKeyProvider(EncryptDataProperties encryptDataProperties) {
        this.encryptDataProperties = encryptDataProperties;
    }

    @Override
    public EncryptKey getKey() {
        EncryptKey encryptKey = new EncryptKey();
        encryptKey.setAesKey(encryptDataProperties.getAes());
        encryptKey.setDesKey(encryptDataProperties.getDes());
        encryptKey.setRsaKey(encryptDataProperties.getRsa());
        encryptKey.setSm2Key(encryptDataProperties.getSm2());
        encryptKey.setSm4Key(encryptDataProperties.getSm4());
        return encryptKey;
    }

}
