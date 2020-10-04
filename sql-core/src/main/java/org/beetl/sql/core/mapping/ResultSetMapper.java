package org.beetl.sql.core.mapping;

import org.beetl.sql.annotation.entity.ResultProvider;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 用户自定义映射方式，参考{@code ResultProvider} 通过注解实现
 *
 * @param <T>
 * @see ResultProvider
 * @see org.beetl.sql.annotation.entity.ProviderConfig
 * @see SimpleJoinMappper
 *
 */
public interface ResultSetMapper<T> {
    /**
     * 将数据库查询结果集ResultSet映射到一个对象上，对象通过target指定
     * @param ctx
     * @param target
     * @param resultSet
     * @param config  实现了ProviderConfig注解的注解，如果没有，则为空
     * @return
     */
    public List<T> mapping(ExecuteContext ctx, Class target, ResultSet resultSet, Annotation config) throws SQLException;
}
