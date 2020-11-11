package org.beetl.sql.sega.common.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.annotation.Template;
import org.beetl.sql.sega.common.SegaSqlBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在SegaMapper方法上使用此注解，申明sql语句来自此注解
 * <pre>{@code
 * @SegaUpdateSql(sql="", rollback="")
 * public void addOneStock(int id);
 *
 * }</pre>
 * @author xiandafu
 * @see Template
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(SegaSqlBuilder.class)
public @interface Rollback {


	/**
	 *  更新语句
	 * @return sql
	 */
	Class provider() ;





}