package com.wangyi.component.encrypt.data.handler;

public interface EncryptHandler {

    boolean support(String encryptType);

    String encrypt(EncryptBody body);

    String decrypt(EncryptBody body);

}
