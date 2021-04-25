package org.beetl.sql.test.annotation;

import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * 每个租户一个库
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Builder(SchemaTenantContext.class)
public @interface SchemaTenant {

}