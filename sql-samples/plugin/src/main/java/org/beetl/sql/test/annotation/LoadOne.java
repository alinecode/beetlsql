package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示加载bean前后需要做的处理
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Builder(MyBeanConvertBuilder.class)
public @interface LoadOne {
    String name() default "";
}
