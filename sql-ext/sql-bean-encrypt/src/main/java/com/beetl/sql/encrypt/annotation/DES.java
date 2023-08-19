package com.beetl.sql.encrypt.annotation;

import com.beetl.sql.encrypt.builder.DESConvert;
import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(DESConvert.class)
public  @interface DES {

}
