package org.beetl.sql.fetch.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.fetch.FetchByTableAction;
import org.beetl.sql.fetch.FetchSqlAction;

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
@Builder(FetchByTableAction.class)
public @interface FetchByTable {
	//中间表对应的类名
	Class tableClass();
	/**
	 * 中间表的属性名字，同使用FetchByTable类的主键的属性
	 */
	String fromAttr();
	String toAttr();
	String enableOn() default  "";
}
