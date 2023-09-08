package com.wangyi.component.encrypt.api.handler.des;

import cn.hutool.crypto.SecureUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.handler.EncryptBody;
import com.wangyi.component.encrypt.api.handler.EncryptHandler;
import com.wangyi.component.encrypt.api.handler.aes.AesKey;
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
public class DesEncryptHandler implements EncryptHandler {

    private final EncryptKeyProvider encryptKeyProvider;

    @Override
    public boolean support(EncryptType encryptType) {
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
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        EncryptKey encryptKey = encryptKeyProvider.getKey(request);
        if (null == encryptKey) {
            return null;
        }
        return encryptKey.getDesKey();
    }

}
