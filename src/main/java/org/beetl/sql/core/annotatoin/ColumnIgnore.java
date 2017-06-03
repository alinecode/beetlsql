package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * beetlsql 内置的插入和更新的时候使用,默认是insert:ture,update:false
 * 新版本2.8.13后建议使用@UpdateIgnore 和 @InsertIgnore
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.FIELD})
public @interface ColumnIgnore {
	public boolean insert() default true;
	public boolean update() default false;
	
}


