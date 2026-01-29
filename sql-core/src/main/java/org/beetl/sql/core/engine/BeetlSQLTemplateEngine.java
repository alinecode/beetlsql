package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.om.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 创建一个BeetlSQL的模板引擎
 */
public class BeetlSQLTemplateEngine extends DefaultTemplateEngine {
	protected  SQLManager sqlManager;
	public BeetlSQLTemplateEngine() {
		super();
		AABuilder.defaultAAFactory = new BeetlSQLAAFactory();
	}

	public void setSqlManager(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator(sqlManager);

		return grammar;
	}

	public static class BeetlSQLAAFactory  extends DefaultAAFactory{
		static BeetlSQLAttributeAccess instance  = new BeetlSQLAttributeAccess();
		static  ReflectBeanAA reflectBeanAA = ReflectBeanAA.INSTANCE;
		@Override
		protected AttributeAccess registerClass(Class c) {
			if(hasMethod(c,"get",Object.class)||hasMethod(c,"get",String.class)){
				//如果有get方法，认为是General GetBean
				classAttrs.put(c, this.reflectBeanAA);
				return reflectBeanAA;
			}
			classAttrs.put(c, this.instance);
			return this.instance;
		}

		public static boolean hasMethod(Class c, String methodName, Class... paras) {
			try {

				Method m = c.getMethod(methodName, paras);
				return true;
			} catch (SecurityException | NoSuchMethodException e) {
				return false;
			}
		}
	}

	public static class BeetlSQLAttributeAccess extends AttributeAccess {

		@Override
		public Object value(Object o, Object name) {
			return BeanKit.getBeanProperty(o,(String)name);
		}
		@Override
		public void setValue(Object o, Object name, Object value){
			BeanKit.setBeanProperty(o,value,(String)name);
		}
	}

}
