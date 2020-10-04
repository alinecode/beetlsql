package org.beetl.sql.annotation.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 广泛用于BeetlSQL注解扩展。定义注解的注解，
 * 用于Annotation的执行,如果用户自定义注解上有这个注解，那么BeetlSQL会按照要求执行
 * <pre>{@code
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target(value = {ElementType.METHOD, ElementType.FIELD})
 * @Builder(Xxxx.class)
 * public @interface UpdateTime {
 *
 * }
 *
 * }</pre>
 *
 * 这里Xxxx类必须是如下类的子类，以实现不同的操作
 * Convert
 * @author xiandafu
 * @see  Fetch
 * @see UpdateTime
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.ANNOTATION_TYPE})
public @interface Builder {
	Class value();
}


