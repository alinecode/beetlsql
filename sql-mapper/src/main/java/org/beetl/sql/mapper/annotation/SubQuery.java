package org.beetl.sql.mapper.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.lambda.SubQueryBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法返回一个LambdaQuery的子类LambdaSubQuery，包含了子查询语句，可以返回LambdaQuery的优势，也能提供灵活的SQL
 * @see "https://gitee.com/xiandafu/beetlsql/issues/I1WRHZ"
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(SubQueryBuilder.class)
public @interface SubQuery {
}
