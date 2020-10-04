package org.beetl.sql.annotation.entity;


import org.beetl.sql.core.mapping.RowMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定一个负责映射的类，此类应该实现{@link RowMapper}接口
 * <pre>{@code
 *
 * @RowProvider(MyUserDepartmentRowProvider.class)
 * public class UserDepartment{
 *
 * }
 *
 * }</pre>
 * @author xiandafu
 * @see RowMapper
 * @see ResultProvider ,通过xml配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RowProvider {
	Class<? extends RowMapper>  value();
}


