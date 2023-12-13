-- 采用 database 存储国际化信息表结构
create table i18n (
    id bigint(20) primary key comment 'ID',
    type varchar(255) not null comment '类型, result_code, front_page',
    language varchar(255) not null comment '语言, zh-CN, en-US',
    code varchar(255) not null comment '编码',
    value varchar(500) not null default '' comment '国际化信息',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    unique uq_type_language_code(type, language, code)
) comment '国际化信息';
