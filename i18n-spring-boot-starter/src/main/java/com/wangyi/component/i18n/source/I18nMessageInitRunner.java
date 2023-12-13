package com.wangyi.component.i18n.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

@RequiredArgsConstructor
@Slf4j
public class I18nMessageInitRunner implements CommandLineRunner {

    private final I18nMessageSource i18nMessageSource;

    @Override
    public void run(String... args) throws Exception {
        i18nMessageSource.initMessage();
    }

}
