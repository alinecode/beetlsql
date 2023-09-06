package org.beetl.sql.test.jpa;

import org.beetl.sql.annotation.builder.Builder;
import org.beetl.sql.test.jpa.JPAMapperMethodParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Builder(JPAMapperMethodParser.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JPASupport {
}
