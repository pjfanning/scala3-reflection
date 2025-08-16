package com.github.pjfanning.scala3_reflection;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnno {
    int idx() default 0;
}
