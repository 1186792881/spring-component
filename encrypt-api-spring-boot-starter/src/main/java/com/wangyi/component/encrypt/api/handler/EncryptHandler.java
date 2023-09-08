package com.wangyi.component.encrypt.api.handler;

import com.wangyi.component.encrypt.api.enums.EncryptType;

public interface EncryptHandler {

    boolean support(EncryptType encryptType);

    String encrypt(EncryptBody body);

    String decrypt(EncryptBody body);

}
