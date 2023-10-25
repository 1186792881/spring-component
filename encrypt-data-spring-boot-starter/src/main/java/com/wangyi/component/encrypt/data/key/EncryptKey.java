package com.wangyi.component.encrypt.data.key;

import com.wangyi.component.encrypt.data.handler.aes.AesKey;
import com.wangyi.component.encrypt.data.handler.des.DesKey;
import com.wangyi.component.encrypt.data.handler.rsa.RsaKey;
import com.wangyi.component.encrypt.data.handler.sm2.Sm2Key;
import com.wangyi.component.encrypt.data.handler.sm4.Sm4Key;
import lombok.Data;

@Data
public class EncryptKey {

    private AesKey aesKey;

    private DesKey desKey;

    private RsaKey rsaKey;

    private Sm2Key sm2Key;

    private Sm4Key sm4Key;

}
