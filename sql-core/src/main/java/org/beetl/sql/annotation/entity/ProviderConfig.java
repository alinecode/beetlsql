package org.beetl.sql.annotation.entity;

import org.beetl.sql.core.mapping.ResultSetMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于Annotation的执行,如果用户自定义注解上有这个注解，那么BeetlSQL会按照要求执行
 * <pre>{@code
 * @Retention(RetentionPolicy.RUNTIME)
 * @Target(value = {ElementType.METHOD, ElementType.FIELD})
 * @ProviderConfig()
 * public @interface JoinConfig {
 *	String joinBy() default "-"
 * }
 *
 * }</pre>
 *
 * java pojo 可以使用此注解辅助映射
 * <pre>{@code
 *
 * @JoinConfig(joinBy="")
 * @RowProvider(XXXRowerMapping.class)
 * public class UserDepartment{
 *
 * }
 *
 * }</pre>
 *
 * 那么JoinConfig配置会传递给XXXRowerMapping使用,d
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.ANNOTATION_TYPE})
public @interface ProviderConfig {
}


