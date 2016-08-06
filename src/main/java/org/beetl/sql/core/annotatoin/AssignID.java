package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AssignID  {
	public String value() default "";
	public String param() default "";
}


