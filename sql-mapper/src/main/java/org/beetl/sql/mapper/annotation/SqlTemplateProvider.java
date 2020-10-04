package org.beetl.sql.mapper.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.provider.ProviderMapperExtBuilder;
import org.beetl.sql.mapper.provider.SqlTemplatePMI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供一个SqlProvider注解，让特定的Provider类来提供动态sql
 * 为那些实在是不想写独立的sql文件，但是又有比较复杂的动态sql的人准备的
 * <pre>{@
 *  @SqlTemplateProvider(XXX.class)
*   public List<User> queryByCondition(Object para1,Object para2)
  *
 * }</pre>
 * BeetlSQL会调用 XXX.class的queryByCondition方法,期望返回一个sql模板语句作为此次查询的sql模板语句
 * @author darren
 * @see  SqlProvider
 * @see  SqlTemplatePMI
 * @see  ProviderMapperExtBuilder
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(ProviderMapperExtBuilder.class)
public @interface SqlTemplateProvider {

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
