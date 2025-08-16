package com.github.pjfanning.scala3_reflection;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassAnno {
    String name();
}