package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlStatement {
	
	/**
	 *
	 * @return 参数名列表
	 */
	
	String params() default "";

	/**
	 *
	 * 
	 * @return statement类型.
	 */
	SqlStatementType type() default SqlStatementType.AUTO;
	
	/**
	 * @return 返回类型，默认是Mapper类的泛型，需要特别声明才用这个
	 */
	@Deprecated
	Class returnType() default Void.class;


}