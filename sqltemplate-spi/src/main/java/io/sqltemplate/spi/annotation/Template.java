package io.sqltemplate.spi.annotation;

public @interface Template {

    String value();

    TemplateType type() default TemplateType.DIR;
}
