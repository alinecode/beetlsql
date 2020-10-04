package org.beetl.sql.mapper.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.provider.ProviderMapperExtBuilder;
import org.beetl.sql.mapper.provider.SqlTemplatePMI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code @Sql}注解提供的sql缺少动态性，可以使用SqlProvider注解，让特定的Provider类来提供动态sql
 * 为那些实在是不想写独立的sql文件，但是又有比较复杂的动态sql的人准备的
 * <pre>{@code
 *     @SqlProvider(UserQueryProvider.class)
 *     public User query(Integer id)
 * }</pre>
 *
 * <pre>
 *     class UserQueryProvider{
 *        SqlReady query(Integer id){
 *            .....
 *        }
 *     }
 *
 * </pre>
 *
 * @author darren
 * @see  SqlTemplatePMI
 * @see  ProviderMapperExtBuilder
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(ProviderMapperExtBuilder.class)
public @interface SqlProvider {

	/**
	 * 由哪个类来提供生成动态的SQL，这个类的方法名需要与mapper同名，否则，需要使用method属性来说明是哪个类方法
	 * 且此类方法的返回值是SqlReady
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
