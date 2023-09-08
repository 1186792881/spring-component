package com.wangyi.component.encrypt.api.key;

import com.wangyi.component.encrypt.api.config.EncryptProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认的密钥提供者, 通过配置文件配置密钥
 */
public class DefaultEncryptKeyProvider implements EncryptKeyProvider {

    private final EncryptProperties encryptProperties;

    public DefaultEncryptKeyProvider(EncryptProperties encryptProperties) {
        this.encryptProperties = encryptProperties;
    }

    @Override
    public EncryptKey getKey(HttpServletRequest request) {
        EncryptKey encryptKey = new EncryptKey();
        encryptKey.setAesKey(encryptProperties.getAes());
        encryptKey.setDesKey(encryptProperties.getDes());
        encryptKey.setRsaKey(encryptProperties.getRsa());
        encryptKey.setSm2Key(encryptProperties.getSm2());
        encryptKey.setSm4Key(encryptProperties.getSm4());
        return encryptKey;
    }

}
