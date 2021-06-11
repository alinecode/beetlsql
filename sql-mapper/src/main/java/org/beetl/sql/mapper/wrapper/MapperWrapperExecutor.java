package org.beetl.sql.mapper.wrapper;

public interface MapperWrapperExecutor {
	 default void before(WrapperContext wrapperContext){
	 	return ;
	 }
	 default void after(WrapperContext wrapperContext,Object ret){
	 	return ;
	 }
}
