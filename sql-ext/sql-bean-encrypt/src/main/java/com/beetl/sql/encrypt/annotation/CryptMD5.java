package com.beetl.sql.encrypt.annotation;

import com.beetl.sql.encrypt.builder.CryptMD5Convert;
import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Builder(CryptMD5Convert.class)
public  @interface CryptMD5 {

}
