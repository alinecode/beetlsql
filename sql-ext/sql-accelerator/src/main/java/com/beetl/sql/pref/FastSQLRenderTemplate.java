package com.beetl.sql.pref;

import org.beetl.core.GroupTemplate;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.om.AABuilder;
import org.beetl.core.om.AsmAAFactory;
import org.beetl.core.statement.*;
import org.beetl.core.statement.optimal.BlockStatementOptimal;
import org.beetl.core.statement.optimal.VarRefOptimal;
import org.beetl.sql.core.engine.BeetlSQLTemplateEngine;
import org.beetl.sql.core.engine.SQLGrammarCreator;

public 	  class FastSQLRenderTemplate extends BeetlSQLTemplateEngine {
	public FastSQLRenderTemplate() {
		super();
	}

	@Override
	protected GrammarCreator getGrammarCreator(GroupTemplate gt) {
		FastGrammarCreator grammar = new FastGrammarCreator();
		return grammar;
	}

	/**
	 * 自定义的语法创建者
	 */
	static class FastGrammarCreator extends SQLGrammarCreator {
		@Override
		public VarRef createVarRef(VarAttribute[] attributes, boolean hasSafe, Expression safe, GrammarToken token,
			GrammarToken firstToken) {
			return (attributes.length == 1 && !hasSafe)
				? new VarRefOptimal(attributes[0], token, firstToken)
				: new VarRef(attributes, hasSafe, safe, firstToken);
		}

		@Override
		public BlockStatement createBlock(Statement[] nodes, GrammarToken token) {
			return (nodes.length == 1) ? new BlockStatementOptimal(nodes, token) : new BlockStatement(nodes, token);
		}
	}

}
