package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlStatement {
	
	/**
	 *  参数名列表，
	 * @return
	 */
	
	String params() default "";

	/**
	 * statement类型.
	 * 
	 * @return
	 */
	SqlStatementType type() default SqlStatementType.AUTO;
	
	
	/**
	 * 采用这个sqlReady，如update xxx set a = ? where id = ?
	 * @return
	 */
	String sqlReady() default "";

}