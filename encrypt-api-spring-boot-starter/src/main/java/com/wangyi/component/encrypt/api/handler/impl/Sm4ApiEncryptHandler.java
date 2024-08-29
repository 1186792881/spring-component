package com.wangyi.component.encrypt.api.handler.impl;

import cn.hutool.crypto.SmUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import com.wangyi.component.encrypt.api.key.EncryptKey;

import java.nio.charset.StandardCharsets;

public class Sm4ApiEncryptHandler extends EncryptHandler {

    public Sm4ApiEncryptHandler(EncryptApiKeyProvider encryptApiKeyProvider) {
        super(encryptApiKeyProvider);
    }

    private EncryptKey getEncryptKey() {
        return getEncryptApiKeyProvider().getEncryptKey(EncryptType.SM4);
    }

    @Override
    public String encrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SmUtil.sm4(encryptKey.getSecretKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body);
    }

    @Override
    public String decrypt(String body) {
        EncryptKey encryptKey = getEncryptKey();
        if (null == encryptKey) {
            return body;
        }
        return SmUtil.sm4(encryptKey.getSecretKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body);
    }

}
