package org.beetl.sql.jooq;

public @interface JooqCodeGen {
	String jdbcURL() default "";
	String userName()default "";
	String password()default "";
	String driver() default "";
	boolean enable() default  true;
}
