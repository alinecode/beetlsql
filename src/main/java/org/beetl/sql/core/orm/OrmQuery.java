package org.beetl.sql.core.orm;

import org.beetl.sql.core.annotatoin.Builder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * 对象映射关系,提供一个懒加载方式加载，如果模板sql文件里已经有，则会做合并
 * 并以sql模板里的优先。
 * <pre>
 *
 &#064;OrmQuery(
 value={
 &#064;OrmCondition(target=Department.class,attr="departmentId",targetAttr="id",type=OrmQuery.Type.ONE),
 &#064;OrmCondition(target=ProductOrder.class,attr="id",targetAttr="userId" ,type=OrmQuery.Type.MANY),
 &#064;OrmCondition(target=Role.class,attr="id",targetAttr="userId" ,sqlId="user.selectRole",type=OrmQuery.Type.MANY)

 }
 )
 * </pre>
 * java代码
 * <pre>
 *
 List&lt;User&gt; list = sql.select("user.queryUsers", User.class, null);
 User user = list.get(0);
 Department dept = (Department)user.get("department");
 System.out.println(dept.getName());
 List&lt;Role&gt; roles  = (List&lt;Role&gt;)user.get("role");
 System.out.println(roles.size());
 * </pre>
 *
 *
 * @author xiandafu
 *
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Builder(value = ORMObjectBuilder.class)
public @interface OrmQuery {
	OrmCondition[] value();


	enum Type {

		ONE, MANY;

		/**
		 * The Constructor.
		 */
		Type() {
		}
	}

}


