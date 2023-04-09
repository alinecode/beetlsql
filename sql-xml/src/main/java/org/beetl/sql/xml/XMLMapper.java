package org.beetl.sql.xml;

import org.beetl.sql.annotation.entity.ProviderConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示XMLConfigMapper 需要的xml映射的配置文件路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ProviderConfig()
public @interface XMLMapper {
	/**
	 * 对应sqlId
	 * @return
	 */
	String resource() default  "";
}

