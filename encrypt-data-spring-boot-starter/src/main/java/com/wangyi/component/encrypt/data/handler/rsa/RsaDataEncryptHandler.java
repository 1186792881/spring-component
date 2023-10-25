package com.wangyi.component.encrypt.data.handler.rsa;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.wangyi.component.encrypt.data.enums.EncryptType;
import com.wangyi.component.encrypt.data.handler.EncryptBody;
import com.wangyi.component.encrypt.data.handler.EncryptHandler;
import com.wangyi.component.encrypt.data.key.EncryptKey;
import com.wangyi.component.encrypt.data.key.EncryptDataKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RsaDataEncryptHandler implements EncryptHandler {

    private final EncryptDataKeyProvider encryptDataKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.RSA.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        RsaKey rsaKey = getRsaKey();
        String bodyStr = body.getBody();
        if (null == rsaKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SecureUtil.rsa(rsaKey.getPrivateKey(), rsaKey.getPublicKey()).encryptBase64(body.getBody(), keyType);
    }

    @Override
    public String decrypt(EncryptBody body) {
        RsaKey rsaKey = getRsaKey();
        String bodyStr = body.getBody();
        if (null == rsaKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SecureUtil.rsa(rsaKey.getPrivateKey(), rsaKey.getPublicKey()).decryptStr(body.getBody(), keyType);
    }

    private RsaKey getRsaKey() {
        EncryptKey encryptKey = encryptDataKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getRsaKey();
    }

}
