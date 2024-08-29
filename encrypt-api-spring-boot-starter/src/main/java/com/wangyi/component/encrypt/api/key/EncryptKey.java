package com.wangyi.component.encrypt.api.key;

import lombok.Data;

@Data
public class EncryptKey {

    /**
     * 非对称加密公钥
     */
    private String publicKey;

    /**
     * 非对称加密私钥
     */
    private String privateKey;

    /**
     * 对称加密密钥
     */
    private String secretKey;

}
