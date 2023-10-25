package com.wangyi.component.encrypt.data.key;

/**
 * 加密密钥提供者, 实现该接口可以自定义获取密钥的方式
 */
public interface EncryptDataKeyProvider {

    /**
     * 获取加密密钥
     * 可以根据线程上下文的信息, 获取密钥
     * @return 返回null时不进行加解密处理
     */
    EncryptKey getKey();

}
