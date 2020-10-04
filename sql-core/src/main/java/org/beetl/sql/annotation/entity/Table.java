package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象对应的数据库表明，默认NameConversion负责转化类名到表明，如果有特殊情况，
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

}


