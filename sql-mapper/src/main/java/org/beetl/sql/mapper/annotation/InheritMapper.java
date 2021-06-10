package org.beetl.sql.mapper.annotation;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于方便的组织sql,用在mapper的方法上，当在接口的mapper方法使用此注解的时候，表示此方法会被子类继承，调用此方法寻找的sql位置应该是子类的sql文件，而不是定义的接口
 * <pre>{@code
 * public interface CommonMapper extends BaseMapper{
 *     @InheritMapper
 *     public List query();
 * }
 *
 * public interface UserMapper extend CommonMapper{
 *
 * }
 *
 * 当调用UserMapper的query方法时候，系统默认会寻找user.sql 而不是common.sql,
 *
 * }</pre>
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritMapper {


}