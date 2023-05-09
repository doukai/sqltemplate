package io.sqltemplate.spi.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Template {

    String value();

    TemplateType type() default TemplateType.DIR;
}
