package com.wangyi.component.base.function;

/**
 * 国际化数据源, 实现类需要将其注入到spring容器中
 */
public interface MessageSource {

    /**
     * 根据错误码和语言查询国际化信息
     * @param code
     * @param language
     * @return
     */
    String getMessage(String code, String language);
}
