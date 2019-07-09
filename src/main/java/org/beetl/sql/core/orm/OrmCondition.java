package org.beetl.sql.core.orm;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 对象对应的数据库表明，默认通过类名，也可以通过此指定
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OrmCondition {

	Class target();

	String attr();

	String targetAttr();

	String sqlId() default "";

	String alias() default "";

	OrmQuery.Type type() default OrmQuery.Type.MANY;

	boolean lazy() default false;

}


