package org.beetl.sql.core.annotatoin;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列名，通常只需要NameConversion来负责转化 除非有特殊的命名，可以使用这个
 * <pre>
 * @Column("id_")
 * private Integer id;
 * </pre>
 * @author xiandafu
 * @see Table
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Column {
    String value();
}

