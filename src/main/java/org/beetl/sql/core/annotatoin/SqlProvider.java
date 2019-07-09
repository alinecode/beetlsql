package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供一个SqlProvider注解，让特定的Provider类来提供动态sql
 * 为那些实在是不想写独立的sql文件，但是又有比较复杂的动态sql的人准备的
 * @author darren
 * @date 2019/4/29 10:20
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlProvider {

	/**
	 * 由哪个类来提供生成动态的SQL
	 * @return 类
	 */
	Class<?> provider();

	/**
	 * 由provider类的哪个方法来提供动态SQL
	 * 不设置则由取当前被注解方法的同名方法
	 * @return 方法名
	 */
	String method() default "";


}
