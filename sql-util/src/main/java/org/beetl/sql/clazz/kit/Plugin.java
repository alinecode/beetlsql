package org.beetl.sql.clazz.kit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此标注的类或者接口可以用扩展，需要参看源码或者文档查看如何扩展
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value = {ElementType.TYPE})
public @interface Plugin {
	String value() default "";
}


