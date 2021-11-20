package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;

/**
 * 创建一个BeetlSQL的模板引擎
 */
public class BeetlSQLTemplateEngine extends DefaultTemplateEngine {


	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator();

		return grammar;
	}


}
