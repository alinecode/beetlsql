package org.beetl.sql.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示存储过程中的参数的顺序，对于In来说，可以使用，按照参数顺序指定
 *  <pre>@{code
 *     @Call("....")
 *      public List<User> call(Integer id,String name)
 *  }</pre>
 *  与下面是一样的
 *  <pre>@{code
 *     @Call("....")
 *      public List<User> call(@CallParam(1) Integer id,@CallParam(2) String name)
 *  }</pre>
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface CallParam {
	int value();
	//非必须
	int jdbcType() default  Integer.MAX_VALUE;
}
