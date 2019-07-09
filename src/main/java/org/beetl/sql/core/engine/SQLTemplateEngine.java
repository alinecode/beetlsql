package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.engine.DefaultTemplateEngine;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.statement.Program;

import java.io.Reader;
import java.util.Map;

public class SQLTemplateEngine extends DefaultTemplateEngine {
	public Program createProgram(Resource resource, Reader reader, Map<Integer, String> textMap, String cr,
			GroupTemplate gt) {
		Program program = super.createProgram(resource, reader, textMap, cr, gt);

		return program;
	}

	@Override
	protected GrammarCreator getGrammerCreator(GroupTemplate gt) {
		GrammarCreator grammar = new SQLGrammarCreator();

		return grammar;
	}


}
