package org.beetl.sql.fetech.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该类的某些属性可以通过再次查询获取
 * <pre>{@code
 *     @Fetch
 *     public class User{
 *     	  private Long id;
 *     	  private Long departmentId
 *        @FetchOne("deparmtId")
 *        Department department;
 *		  @FetchMany("userId")
 *		  List<AuditLog> logs;
 *     }
 *
 * }</pre>
 *
 * 实现类应实现合并查询，以提高性能。
 * @author xiandafu
 * @see org.beetl.sql.fetech.FetchOneAction
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface FetchOne {
	/**
	 * 被抓取的对象通过本对象的哪些属性获得，通常是主键，如果是主键，则调用{@link org.beetl.sql.core.SQLManager#unique}方法获取，
	 * 如果非主键，则调用{@link org.beetl.sql.core.SQLManager#template}方法获取
	 * 注意:BeetlSQL会做一定优化合并查询,则取决于Fetch框架如何实现
	 * @return
	 */
	String value();


}


