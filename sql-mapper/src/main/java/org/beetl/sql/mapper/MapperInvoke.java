package org.beetl.sql.mapper;

import org.beetl.sql.core.SQLManager;

import java.lang.reflect.Method;

/**
 * mapper中的每个方法都会对应一个MapperInvoke类
 * 如下java包中提供各种实现
 * <ul>
 *     <li>internal:自带的实现，如简单的CRUD，{@code BaseMapper} 使用了这个包下
 *     所有代码，比如insert,updateById,deleteById,all,templateOne
 *     </li>
 *     <li>ready:注解提供jdbc sql语句
 *     <pre>{@code
*      @Sql("update user set status=? where id=?")
*      public int updateUser(int status,int id);
*       }
 *     </pre>
 *     </li>
 *     <li>template:注解提供sql模板语句语句
 *     <pre>{@code
 *      @TemplateSql("update user set status=#status# where id=#id#")
 *       public int updateUser(int id,int status);
 *       }
 *       </pre>
 *     </li>
 *     <li>identity:sql文件提供sql语句
 *     <pre>{@code
 *      // 寻找markdown里标题为updateUser的sql语句
 *       public int updateUser(int id,int status);
 *
 *       }</pre>
 *     </li>
 *     <li>springdata:一种类似springdata方式的实现，系统扩展
 *      <pre>{@code
 *      // springData风格，根据方法名自动拼写出sql语句
 *       public int updateUserStatusById(int id,int status);
 *       }
 *     </li>
 *     <li>provider:指定类实现sql语句，是系统扩展
 *      <pre>{@code
 *       @SqlProvider(XXX.class)
 *       public int updateUserStatusById(int id,int status);
 *       }
 *     </li>
 * </ul>
 *
 * MapperInvoke实现类应该是单例和线程安全，可以通过{@code @Builder}注解来扩展新的注解，参考{@Link SpringData}
 * @author xiandafu
 */
public abstract class MapperInvoke {
    public abstract Object call(SQLManager sm, Class entityClass, Method m, Object[] args);
}
