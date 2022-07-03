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
 *      public List<User> call(@CallIndex(1) Integer id,@CallIndex(2) String name)
 *  }</pre>
 * @author zhoupan.
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CallIndex {
	int value();
	//非必须
	int jdbcType() ;
}
