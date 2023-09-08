package com.wangyi.component.encrypt.api.handler.aes;

import cn.hutool.crypto.SecureUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.key.EncryptKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AesEncryptHandler implements EncryptHandler {

    private final EncryptKeyProvider encryptKeyProvider;

    @Override
    public boolean support(EncryptType encryptType) {
        return EncryptType.AES.equals(encryptType);
    }

    @Override
    public String encrypt(EncryptBody body) {
        return SecureUtil.aes(getAesKey().getKey().getBytes(StandardCharsets.UTF_8))
                .encryptBase64(body.getBody());
    }

    @Override
    public String decrypt(EncryptBody body) {
        return SecureUtil.aes(getAesKey().getKey().getBytes(StandardCharsets.UTF_8))
                .decryptStr(body.getBody());
    }

    private AesKey getAesKey() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        return encryptKeyProvider.getKey(request).getAesKey();
    }

}
