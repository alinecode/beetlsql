package org.beetl.sql.annotation.builder;



import org.beetl.sql.annotation.builder.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于属性字段上，在插入或者更新的时候,生成一个当前时间，实现类是{@link UpdateTimeConvert}
 *
 * <pre>{@code
 * @UpdateTime
 * private Date createDate;
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
//执行类
@Builder(UpdateTimeConvert.class)
public @interface UpdateTime {
	FillStrategy value() default FillStrategy.INSERT_UPDATE ;
}


