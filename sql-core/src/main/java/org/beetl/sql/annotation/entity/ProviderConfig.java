package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供给provider注解的配置信息注解<br/>
 * 用于辅助{@link ResultProvider} 和 {@link RowProvider} <br/>
 * 例如，随着{@link RowProvider} 同时在实体类上进行注解<br/>
 * 可以在{@link org.beetl.sql.core.mapping.RowMapper}接口的{@link org.beetl.sql.core.mapping.RowMapper#mapRow}第四个参数接收<br/>
 * <pre>
 *     {@code
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
 * 那么JoinConfig配置会传递给XXXRowerMapping使用
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.ANNOTATION_TYPE})
public @interface ProviderConfig {
}