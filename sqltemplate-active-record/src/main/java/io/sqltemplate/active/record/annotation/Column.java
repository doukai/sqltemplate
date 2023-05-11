package io.sqltemplate.active.record.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    String value();

    boolean key() default false;
}
