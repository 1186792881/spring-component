package com.wangyi.component.encrypt.api.enums;

public enum EncryptType {

    AES("AES", "AES加密"),
    DES("DES", "DES加密"),
    RSA("RSA", "RSA加密"),
    SM2("SM2", "SM2加密"),
    SM4("SM4", "SM4加密");

    private final String value;
    private final String name;

    EncryptType(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
