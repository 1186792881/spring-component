package com.wangyi.component.encrypt.api.key;

import javax.servlet.http.HttpServletRequest;

/**
 * 加密密钥提供者, 实现该接口可以自定义获取密钥的方式
 */
public interface EncryptKeyProvider {

    /**
     * 获取加密密钥
     * @return
     */
    EncryptKey getKey(HttpServletRequest request);

}
