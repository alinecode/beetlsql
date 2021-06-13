package org.beetl.sql.mapper.proxy;

import org.beetl.sql.mapper.provider.ProviderMapperExtBuilder;
import org.beetl.sql.mapper.provider.SqlTemplatePMI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lijiazhi(xiadnafu)
 * @see  MapperProxyConfigBuilder
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperProxy {

	/**
	 *
	 */
	Class<? extends MapperProxyExecutor> value();



}
