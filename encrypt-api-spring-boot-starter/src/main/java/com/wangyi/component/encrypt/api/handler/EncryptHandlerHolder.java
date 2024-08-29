package com.wangyi.component.encrypt.api.handler;

import cn.hutool.core.util.ReflectUtil;
import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.key.EncryptApiKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class EncryptHandlerHolder {

    private final EncryptApiKeyProvider encryptApiKeyProvider;
    private final Map<String, EncryptHandler> encryptHandlerMap = new ConcurrentHashMap<>();

    /**
     * 获取加密Handler
     * @param encryptType
     * @return
     */
    public EncryptHandler getEncryptHandler(EncryptType encryptType) {
        return encryptHandlerMap.computeIfAbsent(encryptType.getValue(), (key) -> {
            Class<? extends EncryptHandler> clazz = encryptType.getHanderClass();
            return ReflectUtil.newInstance(clazz, encryptApiKeyProvider);
        });
    }

}
