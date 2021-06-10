package org.beetl.sql.mapper.wrapper;

import org.beetl.sql.mapper.provider.ProviderMapperExtBuilder;
import org.beetl.sql.mapper.provider.SqlTemplatePMI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author darren
 * @see  SqlTemplatePMI
 * @see  ProviderMapperExtBuilder
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperWrapper {

	/**
	 *
	 */
	Class<? extends MapperWrapperExecutor> value();



}
