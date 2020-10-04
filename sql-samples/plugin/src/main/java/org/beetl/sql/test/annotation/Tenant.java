package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 组合注解，给相关操作添加额外的租户信息，从而实现根据租户分表或者分库
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Builder(TenantContext.class)
public @interface Tenant {

}
