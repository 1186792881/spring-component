package com.wangyi.component.encrypt.api.handler.sm4;

import cn.hutool.crypto.SmUtil;
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
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class Sm4EncryptHandler implements EncryptHandler {

    private final EncryptKeyProvider encryptKeyProvider;

    @Override
    public boolean support(EncryptType encryptType) {
        return EncryptType.SM4.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        Sm4Key sm4Key = getSm4Key();
        if (null == sm4Key) {
            return body.getBody();
        }
        return SmUtil.sm4(sm4Key.getKey().getBytes(StandardCharsets.UTF_8)).encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        Sm4Key sm4Key = getSm4Key();
        if (null == sm4Key) {
            return body.getBody();
        }
        return SmUtil.sm4(sm4Key.getKey().getBytes(StandardCharsets.UTF_8)).decryptStr(body.getBody());
    }

    private Sm4Key getSm4Key() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        EncryptKey encryptKey = encryptKeyProvider.getKey(request);
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getSm4Key();
    }

}
