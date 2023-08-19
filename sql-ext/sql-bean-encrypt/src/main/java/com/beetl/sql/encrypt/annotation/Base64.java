package com.beetl.sql.encrypt.annotation;

import com.beetl.sql.encrypt.builder.Base64Convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.beetl.sql.annotation.builder.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(Base64Convert.class)
public  @interface Base64 {

}
