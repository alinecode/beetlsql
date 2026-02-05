package org.beetl.sql.sugar;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;

public class SugarSQLTemplateEngine extends BeetlSQLTemplateEngine {
	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		SugarSQLGrammarCreator grammar = new SugarSQLGrammarCreator(sqlManager);
		return grammar;
	}
}
