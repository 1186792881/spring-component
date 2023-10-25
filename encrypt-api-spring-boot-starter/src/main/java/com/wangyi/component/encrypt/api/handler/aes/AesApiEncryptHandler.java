package com.wangyi.component.encrypt.api.handler.aes;

import cn.hutool.crypto.SecureUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptKey;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AesApiEncryptHandler implements EncryptHandler {

    private final EncryptApiKeyProvider encryptKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.AES.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        AesKey aesKey = getAesKey();
        if (null == aesKey) {
            return body.getBody();
        }
        return SecureUtil.aes(aesKey.getKey().getBytes(StandardCharsets.UTF_8))
                .encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        AesKey aesKey = getAesKey();
        if (null == aesKey) {
            return body.getBody();
        }
        return SecureUtil.aes(aesKey.getKey().getBytes(StandardCharsets.UTF_8))
                .decryptStr(body.getBody());
    }

    private AesKey getAesKey() {
        EncryptKey encryptKey = encryptKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getAesKey();
    }

}
