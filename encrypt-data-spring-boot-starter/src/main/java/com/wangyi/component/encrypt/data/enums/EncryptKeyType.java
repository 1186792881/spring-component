package com.wangyi.component.encrypt.data.enums;

import cn.hutool.crypto.asymmetric.KeyType;
import org.springframework.util.Assert;

import java.util.Objects;

public enum EncryptKeyType {

    /**
     * 公钥
     */
    PUBLIC_KEY(1),

    /**
     * 私钥
     */
    PRIVATE_KEY(2),

    /**
     * 密钥
     */
    SECRET_KEY(3);

    private final int value;

    EncryptKeyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public KeyType toKeyType() {
        KeyType keyType = null;
        if (Objects.equals(EncryptKeyType.PRIVATE_KEY.getValue(), this.getValue())) {
            keyType = KeyType.PrivateKey;
        } else if (Objects.equals(EncryptKeyType.PUBLIC_KEY.getValue(), this.getValue())) {
            keyType = KeyType.PublicKey;
        } else if (Objects.equals(EncryptKeyType.SECRET_KEY.getValue(), this.getValue())) {
            keyType = KeyType.SecretKey;
        }
        Assert.notNull(keyType, "EncryptKeyType 未指定");
        return keyType;
    }
}
