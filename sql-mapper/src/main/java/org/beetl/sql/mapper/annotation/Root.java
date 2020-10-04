package org.beetl.sql.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在多个参数中，通过@Root注解注明的参数，则在beetlsql执行，能直接用此参数的属性，而不需要加上参数名前缀
 * 如果只有一个参数，则总是"Root"参数
 * @author xiandafu
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Root {

}
