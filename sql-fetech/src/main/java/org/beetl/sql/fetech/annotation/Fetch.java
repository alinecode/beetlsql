package org.beetl.sql.fetech.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.fetech.DefaultBeanFetch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 作用于在pojo上，表示需要fetch操作
 * <pre>{@code
 * @Fetch
 * public class User{
 * private Long id;
 * private Long departmentId;
 * @FetchOne("departmentId")
 * private Department dept
 * }
 * }</pre>
 * 为了防止无限制fetch，可以添加level，指定一个深度。大部分场景按照默认深度1即可。
 * fetch实现避免循环fetch，如果对象已经曾经fetch过，则不会从数据库中再次fetch
 *
 * @author xiandafu
 */

@Builder(DefaultBeanFetch.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface Fetch {
    /**
     * fetch深度，默认为1层，建议使用1或者2，如果fetch深度过深，则会导致大量数据库查询。跟hibernate一样了
     * @return
     */
    int level() default  1;
}
