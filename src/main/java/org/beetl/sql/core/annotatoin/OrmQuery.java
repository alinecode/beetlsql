package org.beetl.sql.core.annotatoin;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 对象映射关系
 * @author xiandafu
 *
 */
@Target({TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface OrmQuery {
	public OrmCondition[] value();

}


