package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * beetlsql 内置的插入时候使用,忽略此字段
 * 同@ColumnIgnore
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD,ElementType.FIELD})
public @interface UpdateIgnore {
	
}


