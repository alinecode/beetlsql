package org.beetl.sql.jooq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface JooqCodeGen {
	String jdbcURL() default "";
	String userName()default "";
	String password()default "";
	String driver() default "";
	boolean enable() default  true;
}
