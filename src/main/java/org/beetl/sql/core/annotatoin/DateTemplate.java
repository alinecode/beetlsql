package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
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
