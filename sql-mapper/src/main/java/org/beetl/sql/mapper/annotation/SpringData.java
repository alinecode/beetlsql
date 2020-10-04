package org.beetl.sql.mapper.annotation;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.mapper.springdata.SpringDataBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示mapper的方法符合spring data，应该按照这种方式执行sql,参考
 *  {@link "https://docs.spring.io/spring-data/jdbc/docs/2.0.1.RELEASE/reference/html/#jdbc.query-methods"}
 * @author xiandafu
 * @see SpringDataBuilder
 * @See SpringDataSelectMI
 */
@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Builder(SpringDataBuilder.class)
public @interface SpringData {


}
