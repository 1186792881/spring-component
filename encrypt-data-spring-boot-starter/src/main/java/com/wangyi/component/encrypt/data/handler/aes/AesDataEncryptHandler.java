package com.wangyi.component.encrypt.data.handler.aes;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.wangyi.component.encrypt.data.enums.EncryptType;
import com.wangyi.component.encrypt.data.handler.EncryptBody;
import com.wangyi.component.encrypt.data.handler.EncryptHandler;
import com.wangyi.component.encrypt.data.key.EncryptKey;
import com.wangyi.component.encrypt.data.key.EncryptDataKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AesDataEncryptHandler implements EncryptHandler {

    private final EncryptDataKeyProvider encryptDataKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.AES.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        AesKey aesKey = getAesKey();
        String bodyStr = body.getBody();
        if (null == aesKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SecureUtil.aes(aesKey.getKey().getBytes(StandardCharsets.UTF_8))
                .encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        AesKey aesKey = getAesKey();
        String bodyStr = body.getBody();
        if (null == aesKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SecureUtil.aes(aesKey.getKey().getBytes(StandardCharsets.UTF_8))
                .decryptStr(body.getBody());
    }

    private AesKey getAesKey() {
        EncryptKey encryptKey = encryptDataKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getAesKey();
    }

}
