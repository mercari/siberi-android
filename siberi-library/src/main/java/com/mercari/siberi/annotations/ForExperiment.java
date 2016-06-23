package com.mercari.siberi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({
        java.lang.annotation.ElementType.FIELD,
        java.lang.annotation.ElementType.TYPE,
        java.lang.annotation.ElementType.LOCAL_VARIABLE,
        java.lang.annotation.ElementType.METHOD
})
public @interface ForExperiment {
    String value();
}