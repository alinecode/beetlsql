package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.FIELD})
public @interface DateTemplate {
	
	public static String MIN_PREFIX = "min";	
	public static String MAX_PREFIX = "max";
	public static String LESS_OPT = "<";
	public static String LARGE_OPT = ">=";
	
	
	/**
	 * minDate,maxDate
	 * @return
	 */
	String accept() default ""; //默认
	String compare() default "";
	
}
