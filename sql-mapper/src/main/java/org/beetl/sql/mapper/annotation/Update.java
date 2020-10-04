package org.beetl.sql.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个mapper方法是更新语句，如果无此注解，则默认为查询类
 *
 *
 * @author xiandafu
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {

}
