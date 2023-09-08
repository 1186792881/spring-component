package com.wangyi.component.encrypt.api.handler.sm2;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptKey;
import com.wangyi.component.encrypt.api.key.EncryptKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class Sm2EncryptHandler implements EncryptHandler {

    private final EncryptKeyProvider encryptKeyProvider;

    @Override
    public boolean support(EncryptType encryptType) {
        return EncryptType.SM2.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        Sm2Key sm2Key = getSm2Key();
        if (null == sm2Key) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SmUtil.sm2(sm2Key.getPrivateKey(), sm2Key.getPublicKey()).encryptBase64(body.getBody(), keyType);
    }

    @Override
    public String decrypt(EncryptBody body) {
        Sm2Key sm2Key = getSm2Key();
        if (null == sm2Key) {
            return body.getBody();
        }
        KeyType keyType = body.getEncryptKeyType().toKeyType();
        return SmUtil.sm2(sm2Key.getPrivateKey(), sm2Key.getPublicKey()).decryptStr(body.getBody(), keyType);
    }

    private Sm2Key getSm2Key() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        EncryptKey encryptKey = encryptKeyProvider.getKey(request);
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getSm2Key();
    }

}
