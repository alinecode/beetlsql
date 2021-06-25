package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;

public class BeetlSQLTemplateEngine extends DefaultTemplateEngine {


	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator();

		return grammar;
	}


}
