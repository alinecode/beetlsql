package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 另外一种放置额外属性的方式,放在类上的注解，申明用指定的方法来完成放置额外参数
 * @author lijiazhi
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tail  {
	/**
	 * 方法名称，参数必须是string,object
	 * @return
	 */
	public String set() default "set";
}


