package com.wangyi.component.encrypt.api.enums;

import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.handler.impl.AesApiEncryptHandler;
import com.wangyi.component.encrypt.api.handler.impl.DesApiEncryptHandler;
import com.wangyi.component.encrypt.api.handler.impl.RsaApiEncryptHandler;
import com.wangyi.component.encrypt.api.handler.impl.Sm2ApiEncryptHandler;
import com.wangyi.component.encrypt.api.handler.impl.Sm4ApiEncryptHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加密方式
 */

@Getter
@AllArgsConstructor
public enum EncryptType {

    AES("AES", "AES加密", AesApiEncryptHandler.class),
    DES("DES", "DES加密", DesApiEncryptHandler.class),
    RSA("RSA", "RSA加密", RsaApiEncryptHandler.class),
    SM2("SM2", "SM2加密", Sm2ApiEncryptHandler.class),
    SM4("SM4", "SM4加密", Sm4ApiEncryptHandler.class);

    private final String value;
    private final String name;
    private final Class<? extends EncryptHandler> handerClass;

}
