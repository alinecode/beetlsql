package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sql {
	
	

	/**
	 * statement类型.
	 * 
	 * @return
	 */
	SqlStatementType type() default SqlStatementType.AUTO;
	
	
	/**
	 * 采用这个sql，如update xxx set a = ? where id = ?
	 * @return
	 */
	String value() ;

	/**
	 * @return  返回类型，默认是Mapper类的泛型，需要特别声明才用这个
	 */
	Class returnType() default Void.class;
}