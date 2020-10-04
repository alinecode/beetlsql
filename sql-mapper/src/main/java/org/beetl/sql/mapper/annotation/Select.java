package org.beetl.sql.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 标记dao方法是select操作,另外一个 {@code @Update}和 {@code @BatchUpdate}，标识update操作和批量操作
 * 通常beetlsql会自动根据返回值判断，比如返回一个集合或者PageResult，则肯定是select
 *
 * 如果beetlsql无法判断，则需要显示的用注解说明操作类型，总是推荐使用注解，这样易于理解。（TODO，说清楚无法判断场景),则需要用注解说明
 *
 * <pre>{@code
 * @Select
 * public List queryUser();
 * }</pre>
 * @author xiandafu
 * @see Update
 * @see BatchUpdate
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {

}
