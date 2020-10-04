package org.beetl.sql.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数名，通常JDK8启用parameters后，自动能获取到方法参数名字，如果没有启用，可以使用此来表示接口参数名字
 *
 * @author zhoupan.
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
	String value();
}
