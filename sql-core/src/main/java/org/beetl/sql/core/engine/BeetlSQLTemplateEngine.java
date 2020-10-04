package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.statement.*;

import java.io.Reader;
import java.util.Map;

public class BeetlSQLTemplateEngine extends DefaultTemplateEngine {


	@Override
	protected GrammarCreator getGrammerCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator();

		return grammar;
	}





}
