package com.beetl.sql.tenant.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否允许发生重写，作用在RewriteBaseMapper的mapper方法上
 * <pre>
 * ublic interface MyRewriteMapper extends RewriteBaseMapper<OrderLog> {
 * 	List<OrderLog> select(String name);
 *  @DisableRewrite
 *  List<OrderLog> select2(String name);
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface DisableRewrite {

}
