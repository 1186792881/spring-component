package com.wangyi.component.encrypt.api.handler.des;

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
public class DesApiEncryptHandler implements EncryptHandler {

    private final EncryptApiKeyProvider encryptKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.DES.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        DesKey desKey = getDesKey();
        if (null == desKey) {
            return body.getBody();
        }
        return SecureUtil.des(desKey.getKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        DesKey desKey = getDesKey();
        if (null == desKey) {
            return body.getBody();
        }
        return SecureUtil.des(desKey.getKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body.getBody());
    }

    private DesKey getDesKey() {
        EncryptKey encryptKey = encryptKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getDesKey();
    }

}
