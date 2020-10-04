package org.beetl.sql.annotation.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在pojo的属性上，当SQLManager调用viewType传入的类与此属性的View一致的时候，查询结果将包含此属性，否则，不包含
 * <pre>@{
 * @View(Simple.class,All.class)
 * private String name;
 * @View(All.class)
 * private Clob clob;
 * }</pre>
 *
 * 如上例子，当调用SQLManager.viewType(Simple.class).unique()的时候，
 * clob字段不会获取到，生成的sql语句也不会包含clbo字段
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface View {
    Class[] value();
}
