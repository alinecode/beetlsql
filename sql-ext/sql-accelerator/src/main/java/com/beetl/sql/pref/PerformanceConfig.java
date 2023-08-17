package com.beetl.sql.pref;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.om.AABuilder;
import org.beetl.core.om.AsmAAFactory;
import org.beetl.core.statement.*;
import org.beetl.core.statement.optimal.BlockStatementOptimal;
import org.beetl.core.statement.optimal.VarRefOptimal;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrapFactory;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;
import org.beetl.sql.core.engine.SQLGrammarCreator;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.ext.PluginExtConfig;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceConfig implements PluginExtConfig {
	@Override
	public void config(SQLManager sqlManager) {
		BeanProcessor beanProcessor = sqlManager.getDefaultBeanProcessors();
		if(beanProcessor.getClass()!=BeanProcessor.class){
			//如果被定制过，失败
			throw new IllegalStateException("不能修改BeanProcessor:"+beanProcessor.getClass());
		}

		SQLTemplateEngine sqlTemplateEngine  = sqlManager.getSqlTemplateEngine();
		if(!(sqlTemplateEngine instanceof BeetlTemplateEngine)){
			throw new IllegalStateException("不能修改SQLTemplateEngine:"+sqlTemplateEngine.getClass());
		}



		sqlManager.setDefaultBeanProcessors(new FastBeanProcessor());
		sqlManager.setNc(new CachedNameConversion(sqlManager.getNc()));

		BeetlTemplateEngine beetlTemplateEngine = (BeetlTemplateEngine)sqlTemplateEngine;
		beetlTemplateEngine.getBeetl().getGroupTemplate().setEngine(new FastSQLRenderTemplate());

		BeanKit.propertyDescriptorWrapFactory = new PropertyDescriptorWrapFactory() {
			@Override
			public PropertyDescriptorWrap make(Class c, PropertyDescriptor prop, int i) {
				return new FastPropertyDescriptor(c,prop,i);
			}
		};

	}







}
