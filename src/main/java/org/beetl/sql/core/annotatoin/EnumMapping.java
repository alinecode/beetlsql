package org.beetl.sql.core.annotatoin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 对象对应的数据库表明，默认通过类名，也可以通过此指定
 * @author xiandafu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumMapping {
	public String value();
}


