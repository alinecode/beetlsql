package org.beetl.sql.annotation.entity;

import org.beetl.sql.core.mapping.ResultSetMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定一个映射结果集到Java实体类的实现
 * <pre>
 *    {@literal @}ResultProvider(AutoJsonMapper.class)
 *     public class User{
 *			private Integer id;
 *			private Department dept;
 *     }
 * </pre>
 *  AutoJsonMapper.class 应该是{@code ResultSetMapper}的一个子类
 * @author xiandafu
 * @see ResultSetMapper
 * @see org.beetl.sql.core.mapping.join.AutoJsonMapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResultProvider {
	Class<? extends ResultSetMapper> value();
}


