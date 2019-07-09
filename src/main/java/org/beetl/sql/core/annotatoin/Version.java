package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 乐观锁实现。
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Version {

	/**
	 *
	 * @return 默认表示程序指定，否则，beetlsql使用此值作为初始值
	 */
	int value() default -1;

	String param() default "";
}


