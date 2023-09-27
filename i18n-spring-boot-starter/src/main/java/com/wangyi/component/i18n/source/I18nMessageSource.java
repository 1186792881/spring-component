package com.wangyi.component.i18n.source;

import java.util.List;
import java.util.Map;

/**
 * 国际化数据源, 实现类需要将其注入到spring容器中
 */
public interface I18nMessageSource {

    /**
     * 根据code和language查询国际化信息
     * @param type (错误码, 前端页面)
     * @param code 编码
     * @param language 语言编码
     * @return
     */
    String getMessage(String type, String code, String language);

    /**
     * 根据多个code和language查询国际化信息
     * @param type
     * @param codeList
     * @param language
     * @return
     */
    Map<String, String> getMessage(String type, List<String> codeList, String language);

    /**
     * 初始化国际化消息
     */
    default void initMessage() { }
}
