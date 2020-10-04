package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定Json映射的配置，可以是来自文件，或者来自字符串，
 * <pre>{@code
 * @JsonMappingConfig("{'name_':'name'}")
 * public class User{
 *     String name;
 * }
 * }</pre>
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ProviderConfig()
public @interface JsonMapper {
	String value() default "";
	String resource() default  "";
}


