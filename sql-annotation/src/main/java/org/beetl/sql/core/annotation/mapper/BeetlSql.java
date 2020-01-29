package org.beetl.sql.core.annotation.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 采用SQL模板
 * @see  Sql
 * @see SqlProvider
 * @see SqlStatement
 * @see SpringData
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlTemplate {


	/**
	 * statement类型.
	 *
	 * @return SqlStatementType
	 */
	SqlStatementType type() default SqlStatementType.AUTO;


	/**
	 * 采用这个sql，如update xxx set a = #name# where id = #id#
	 * @return sql
	 */
	String value() default "";



}