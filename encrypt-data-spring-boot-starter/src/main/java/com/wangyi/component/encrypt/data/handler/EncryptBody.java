package com.wangyi.component.encrypt.data.handler;

import com.wangyi.component.encrypt.data.enums.EncryptKeyType;
import lombok.Data;

@Data
public class EncryptBody {

    private String body;

    private String encryptType;

    private EncryptKeyType encryptKeyType;
}
