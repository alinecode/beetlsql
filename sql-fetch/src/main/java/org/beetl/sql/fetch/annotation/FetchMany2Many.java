package org.beetl.sql.fetch.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.fetch.FetchMany2ManyAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用中间表实现多对多关联关系
 * <pre>{@code
 *     @Fetch
 *     public class User{
 *     	  private Long id;
 *     	  private Long departmentId
 *        @FetchByTable(tableClass=UserRole.class,fkAttr="userId")
 *        List<Role>  roles;
 *     }
 *
 *    @Fetch
 *    public class Role{
 *       private Long id;
 *       private String name
 *       @FetchByTable(tableClass=UserRole.class,fkAttr="roleId")
 *       List<User>  users;
 *    }
 *    //中间表
 *    @Table(name="user_role")
 *    public class UserRole{
 *       	  private Long id;
 *      	  private Long userId
 *            private Long roleIdId
 *    }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(FetchMany2ManyAction.class)
public @interface FetchMany2Many  {
	//中间表对应的类名
	Class tableClass();
	/**
	 * 中间表的属性名字，同使用FetchByTable类的主键的属性
	 */
	String fromAttr();
	String toAttr();
	/**
	 * 如果设置一个非空值，且sql模板执行上下文包含了此非空值，这Fetch生效。如果为空值，这总是生效，每次都会调用Fetch
	 * @return
	 * @see DynamicFetchEnableOnFunction
	 */
	String enableOn() default "";
}
