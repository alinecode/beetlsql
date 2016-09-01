package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * beetlsql 内置的插入和更新的时候使用,默认是insert:ture,update:false
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnIgnore {
	public boolean insert() default true;
	public boolean update() default false;
	
}


