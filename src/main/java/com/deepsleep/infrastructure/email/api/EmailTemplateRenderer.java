package com.deepsleep.infrastructure.email.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class EmailTemplateRenderer {

    private final SpringTemplateEngine templateEngine;

    private static final String MODEL_NAME = "model";

    public String render(String templateName, Object model) {
        Context context = new Context(Locale.SIMPLIFIED_CHINESE);
        context.setVariable(MODEL_NAME, model);

        return templateEngine.process(templateName, context);
    }
}
