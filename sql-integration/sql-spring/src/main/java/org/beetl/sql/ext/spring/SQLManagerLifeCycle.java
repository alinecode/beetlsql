package org.beetl.sql.ext.spring;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;

import java.util.List;

/**
 * sqlManager 初始化完毕后的回调，用于进一步通过代码定制sqlmanager
 * 此方法调用后，将会进一步初始化bean所有Mapper
 */
public interface SQLManagerLifeCycle {
	/**
	 * 实例化SQLManager之前，调整Builder
	 * @param sqlMangerName
	 * @param builder
	 */
	default void customizeBuild(String sqlMangerName,SQLManagerBuilder builder){}

	/**
	 * 创建sqlmanager后，在初始化Mapper Bean之前
	 * @param sqlManagerName
	 * @param manager
	 */
	void customize(String sqlManagerName , SQLManager manager);

	/**
	 * 准备初始化一个mapper之前，可以收集mapper打印，或者预加载mapper里sql模板
	 * @param sqlManagerName
	 * @param manager
	 * @param mapperClass
	 */
	default Class beforeMapper(String sqlManagerName , SQLManager manager,Class mapperClass){return  mapperClass;}

}

