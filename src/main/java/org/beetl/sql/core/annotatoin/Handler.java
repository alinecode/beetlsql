package org.beetl.sql.core.annotatoin;

import org.beetl.sql.core.handler.HandlerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于Annotation的执行
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.ANNOTATION_TYPE})
public @interface Handler {
    public Class value() ;
    public int[] accept() default {HandlerType.UPDATE,HandlerType.INSERT};
}


