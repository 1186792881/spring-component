package com.wangyi.component.encrypt.api.handler;

import com.wangyi.component.encrypt.api.enums.EncryptKeyType;
import lombok.Data;

@Data
public class EncryptBody {

    private String body;

    private String encryptType;

    private EncryptKeyType encryptKeyType;
}
