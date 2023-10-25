package com.wangyi.component.encrypt.data.handler.sm4;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
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
public class Sm4DataEncryptHandler implements EncryptHandler {

    private final EncryptDataKeyProvider encryptDataKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.SM4.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        Sm4Key sm4Key = getSm4Key();
        String bodyStr = body.getBody();
        if (null == sm4Key || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SmUtil.sm4(sm4Key.getKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        Sm4Key sm4Key = getSm4Key();
        String bodyStr = body.getBody();
        if (null == sm4Key || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SmUtil.sm4(sm4Key.getKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body.getBody());
    }

    private Sm4Key getSm4Key() {
        EncryptKey encryptKey = encryptDataKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getSm4Key();
    }

}
