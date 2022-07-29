package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象对应的数据库表名，默认NameConversion负责转化类名到表名，如果有特殊情况，
 * 也可以通过此指定
 * <pre>{@code
 * @Table("t_user")
 * public class User{}
 * }</pre>
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	String name();

	/**
	 * 是否是视图。无实际意义，辅助说说明此POJO与视图对应
	 * @return
	 */
	boolean isView() default  false;


	/**
	 * 是否使用pojo定义的id，通常用于视图需要一个id，或者某些表有自己的id，但期望通过此POJO申明的id做一些single，unique查询
	 * 当assignID为true的时候。beetlsql忽略数据库的id定义，以POJO定义为准
	 * @return
	 */
	boolean assignID() default  false;
}


