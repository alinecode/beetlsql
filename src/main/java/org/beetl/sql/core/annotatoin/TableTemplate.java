package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 返回值添加到模板查询的sql语句后,如果没有返回值，则按照主键降序排序，满足大部分应用
 * @author lijiazhi
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableTemplate {
	String value() default "";
}
