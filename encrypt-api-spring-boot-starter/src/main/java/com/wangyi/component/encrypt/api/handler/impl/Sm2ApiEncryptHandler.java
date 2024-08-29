package com.wangyi.component.encrypt.api.handler.impl;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptKey;

public class Sm2ApiEncryptHandler extends EncryptHandler {

    public Sm2ApiEncryptHandler(EncryptApiKeyProvider encryptApiKeyProvider) {
        super(encryptApiKeyProvider);
    }

    private EncryptKey getEncryptKey() {
        return getEncryptApiKeyProvider().getEncryptKey(EncryptType.SM2);
    }

    @Override
    public String encrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SmUtil.sm2(encryptKey.getPrivateKey(), encryptKey.getPublicKey()).encryptBase64(body, KeyType.PrivateKey);
    }

    @Override
    public String decrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SmUtil.sm2(encryptKey.getPrivateKey(), encryptKey.getPublicKey()).decryptStr(body, KeyType.PrivateKey);
    }

}
