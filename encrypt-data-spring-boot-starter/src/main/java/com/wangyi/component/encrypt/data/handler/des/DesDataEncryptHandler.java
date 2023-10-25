package com.wangyi.component.encrypt.data.handler.des;

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
import java.sql.Struct;

@Component
@RequiredArgsConstructor
public class DesDataEncryptHandler implements EncryptHandler {

    private final EncryptDataKeyProvider encryptDataKeyProvider;

    @Override
    public boolean support(String encryptType) {
        return EncryptType.DES.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        DesKey desKey = getDesKey();
        String bodyStr = body.getBody();
        if (null == desKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SecureUtil.des(desKey.getKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        DesKey desKey = getDesKey();
        String bodyStr = body.getBody();
        if (null == desKey || StrUtil.isBlank(bodyStr)) {
            return body.getBody();
        }
        return SecureUtil.des(desKey.getKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body.getBody());
    }

    private DesKey getDesKey() {
        EncryptKey encryptKey = encryptDataKeyProvider.getKey();
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getDesKey();
    }

}
