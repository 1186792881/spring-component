package com.wangyi.component.i18n.core;

import java.util.Map;

/**
 * 国际化数据源, 实现类需要将其注入到spring容器中
 */
public interface I18nMessageSource {

    /**
     * 根据code和语言查询国际化信息
     * @param type (错误码, 前端页面)
     * @param code 编码
     * @param language 语言编码
     * @return
     */
    String getMessage(String type, String code, String language);

    /**
     * 保存 ResultCode 实现类中的错误码和错误消息
     * @param type (错误码, 前端页面)
     * @param codeMsgMap key: 错误码, msg: 默认语言的错误消息
     * @param language 默认的语言编码
     */
    void initMessage(String type, Map<String, String> codeMsgMap, String language);
}
