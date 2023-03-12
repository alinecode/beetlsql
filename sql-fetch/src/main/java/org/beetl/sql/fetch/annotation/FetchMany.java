package org.beetl.sql.fetch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
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
 *
 * }</pre>
 * 取多条,采用模板查询机制，如上，logs的值是通过模板查询获得，模板条件的是logs对象的userId
 * 参考 {@link FetchOne}
 * @author xiandafu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface FetchMany {
	/**
	 * 被抓取的对象通过本对象的哪些属性获得，通常是主键，如果是主键，则调用{@link org.beetl.sql.core.SQLManager#unique}方法获取，
	 * 如果非主键，则调用{@link org.beetl.sql.core.SQLManager#template}方法获取
	 * 注意:BeetlSQL会做一定优化合并查询,则取决于Fetech框架如何实现
	 * @return
	 */
	String value();

	String enableOn() default  "";


}


