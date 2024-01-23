package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.om.AABuilder;
import org.beetl.core.om.AsmAAFactory;
import org.beetl.core.om.AttributeAccess;
import org.beetl.core.om.DefaultAAFactory;
import org.beetl.sql.clazz.kit.BeanKit;

import java.lang.reflect.Modifier;

/**
 * 创建一个BeetlSQL的模板引擎
 */
public class BeetlSQLTemplateEngine extends DefaultTemplateEngine {

	public BeetlSQLTemplateEngine() {
		super();
		AABuilder.defaultAAFactory = new BeetlSQLAAFactory();
	}
	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator();

		return grammar;
	}

	public static class BeetlSQLAAFactory  extends DefaultAAFactory{
		static BeetlSQLAttributeAccess instance  = new BeetlSQLAttributeAccess();
		@Override
		protected AttributeAccess registerClass(Class c) {
			classAttrs.put(c, this.instance);
			return this.instance;
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
