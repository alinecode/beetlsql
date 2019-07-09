package org.beetl.sql.core.annotatoin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mapper 中的sql文件位置，默认根据DefaultSQLIdNameConversion，也可以在这里指定
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface SqlResource {
	String value();
}


