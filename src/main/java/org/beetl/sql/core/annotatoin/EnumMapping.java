package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象对应的数据库表明，默认通过类名，也可以通过此指定
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnumMapping {
	String value() default EnumType.STRING;

	class EnumType {
		public static final String ORDINAL = "ORDINAL";
		public static final String STRING = "STRING";
	}
}


