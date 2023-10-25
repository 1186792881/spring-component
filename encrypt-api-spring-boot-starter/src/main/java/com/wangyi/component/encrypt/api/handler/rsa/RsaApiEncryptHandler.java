package com.wangyi.component.encrypt.api.handler.rsa;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptKey;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RsaApiEncryptHandler implements EncryptHandler {

    private final EncryptApiKeyProvider encryptKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.RSA.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        RsaKey rsaKey = getRsaKey();
        if (null == rsaKey) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SecureUtil.rsa(rsaKey.getPrivateKey(), rsaKey.getPublicKey()).encryptBase64(body.getBody(), keyType);
    }

    @Override
    public String decrypt(EncryptBody body) {
        RsaKey rsaKey = getRsaKey();
        if (null == rsaKey) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SecureUtil.rsa(rsaKey.getPrivateKey(), rsaKey.getPublicKey()).decryptStr(body.getBody(), keyType);
    }

    private RsaKey getRsaKey() {
        EncryptKey encryptKey = encryptKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getRsaKey();
    }

}
