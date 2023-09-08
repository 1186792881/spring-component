package com.wangyi.component.encrypt.api.handler;

import com.wangyi.component.encrypt.api.enums.EncryptType;
import com.wangyi.component.encrypt.api.enums.EncryptKeyType;
import lombok.Data;

@Data
public class EncryptBody {

    private String body;

    private EncryptType encryptType;

    private EncryptKeyType encryptKeyType;
}
