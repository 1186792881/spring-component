package com.wangyi.component.encrypt.api.handler.impl;

import cn.hutool.crypto.SecureUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptKey;

import java.nio.charset.StandardCharsets;

public class AesApiEncryptHandler extends EncryptHandler {

    public AesApiEncryptHandler(EncryptApiKeyProvider encryptApiKeyProvider) {
        super(encryptApiKeyProvider);
    }

    private EncryptKey getEncryptKey() {
        return getEncryptApiKeyProvider().getEncryptKey(EncryptType.AES);
    }

    @Override
    public String encrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SecureUtil.aes(encryptKey.getSecretKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body);
    }

    @Override
    public String decrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SecureUtil.aes(encryptKey.getSecretKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body);
    }

}
