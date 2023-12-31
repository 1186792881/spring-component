package com.wangyi.component.encrypt.api.handler;

public interface EncryptHandler {

    boolean support(String encryptType);

    String encrypt(EncryptBody body);

    String decrypt(EncryptBody body);

}
