package org.beetl.sql.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示为mapper 存储过程的输出参数放到此Bean中
 * <pre>@{code
 *     @Call("....")
 *     public List<User> call(@CallParam(1) Integer id,@CallOutBean  XXXBen bean)
 * }</pre>
 *
 * XXXBen 可以定义如下
 * <pre>@{code
 *     public class XXXBen{
 *        @CallIndex(2) // 存储过程第二个参数
 *        Integer age;
 *     }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CallOutBean {

}

