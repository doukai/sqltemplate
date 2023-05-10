package io.sqltemplate.spi.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Instance {

    String value();

    InstanceType type() default InstanceType.QUERY;
}
