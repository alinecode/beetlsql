package org.beetl.sql.annotation.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记枚举的取值字段，如果是一个外部的Enum，则可以使用此标记，否则，建议使用{@link EnumValue}
 * <pre>{@code
 * @EnumMapping("day")  //取WeeDay的day属性
 * private WeekDay weekDay
 *
 *}</pre>
 *
 * 3.0增加了{@code EnumValue },可以代替此类，无论哪种方法，枚举信息最终记录在ClassAnnotation中
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumMapping {
    String value();
}


