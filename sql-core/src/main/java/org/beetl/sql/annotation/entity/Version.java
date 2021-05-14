package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 乐观锁实现,此注解会使得内置的update语句在where部分加上 此注解的对应的列名
 * <pre>
 * {@code
 * @AssignId
 * private int id
 *
 * @version
 * private int  dataVersion;
 * }</pre>
 *
 * updateById 会生成如下sql语句：
 *
 * <pre>
 * where  id=#id#  and  data_version = #dataVersion#
 *
 * </pre>
 * @author xiandafu
 * @see org.beetl.sql.core.db.AbstractDBStyle#genUpdateById
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Version {

	/**
	 *
	 * @return 默认-1表示程序指定一个版本号，否则，beetlsql使用此值作为初始值
	 */
	int value() default -1;

	String param() default "";
}


