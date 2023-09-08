package com.wangyi.component.encrypt.api.handler.rsa;

import cn.hutool.crypto.SecureUtil;
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
public class RsaEncryptHandler implements EncryptHandler {

    private final EncryptKeyProvider encryptKeyProvider;

    @Override
    public boolean support(EncryptType encryptType) {
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
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        EncryptKey encryptKey = encryptKeyProvider.getKey(request);
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getRsaKey();
    }

}
