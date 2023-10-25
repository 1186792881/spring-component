package com.wangyi.component.encrypt.data.handler.sm2;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
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
public class Sm2DataEncryptHandler implements EncryptHandler {

    private final EncryptDataKeyProvider encryptDataKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.SM2.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        Sm2Key sm2Key = getSm2Key();
        String bodyStr = body.getBody();
        if (null == sm2Key || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SmUtil.sm2(sm2Key.getPrivateKey(), sm2Key.getPublicKey()).encryptBase64(body.getBody(), keyType);
    }

    @Override
    public String decrypt(EncryptBody body) {
        Sm2Key sm2Key = getSm2Key();
        String bodyStr = body.getBody();
        if (null == sm2Key || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SmUtil.sm2(sm2Key.getPrivateKey(), sm2Key.getPublicKey()).decryptStr(body.getBody(), keyType);
    }

    private Sm2Key getSm2Key() {
        EncryptKey encryptKey = encryptDataKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getSm2Key();
    }

}
