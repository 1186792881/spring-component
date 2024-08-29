package com.wangyi.component.encrypt.api.handler;

import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;

public abstract class EncryptHandler {

    private final EncryptApiKeyProvider encryptApiKeyProvider;

    public EncryptHandler(EncryptApiKeyProvider encryptApiKeyProvider) {
        this.encryptApiKeyProvider = encryptApiKeyProvider;
    }

    public EncryptApiKeyProvider getEncryptApiKeyProvider() {
        return encryptApiKeyProvider;
    }

    public abstract String encrypt(String body);

    public abstract String decrypt(String body);

}
