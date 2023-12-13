package com.wangyi.component.i18n.source.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class I18n {

    private Long id;

    private String type;

    private String language;

    private String code;

    private String value;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static final String TABLE_NAME = "i18n";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
