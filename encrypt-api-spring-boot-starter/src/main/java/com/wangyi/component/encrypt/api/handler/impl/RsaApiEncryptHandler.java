package com.wangyi.component.encrypt.api.handler.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptKey;

public class RsaApiEncryptHandler extends EncryptHandler {

    public RsaApiEncryptHandler(EncryptApiKeyProvider encryptApiKeyProvider) {
        super(encryptApiKeyProvider);
    }

    private EncryptKey getEncryptKey() {
        return getEncryptApiKeyProvider().getEncryptKey(EncryptType.RSA);
    }

    @Override
    public String encrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SecureUtil.rsa(encryptKey.getPrivateKey(), encryptKey.getPublicKey()).encryptBase64(body, KeyType.PrivateKey);
    }

    @Override
    public String decrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SecureUtil.rsa(encryptKey.getPrivateKey(), encryptKey.getPublicKey()).decryptStr(body, KeyType.PrivateKey);
    }

}
