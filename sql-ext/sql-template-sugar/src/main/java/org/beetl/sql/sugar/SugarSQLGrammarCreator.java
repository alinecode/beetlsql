package org.beetl.sql.sugar;

import org.beetl.core.statement.*;
import org.beetl.core.statement.optimal.BlockStatementOptimal;
import org.beetl.core.statement.optimal.VarRefOptimal;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.SQLGrammarCreator;
import org.beetl.sql.core.engine.SQLPlaceholderST;


public class SugarSQLGrammarCreator extends SQLGrammarCreator {
	public SugarSQLGrammarCreator(SQLManager sqlManager) {
		super(sqlManager);
	}
	@Override
	public PlaceholderST createTextOutputSt(Expression exp, FormatExpression format) {
		disableSyntaxCheck("TextOutputSt");
		if(format!=null&& ShortHolderFactory.isSupport(format.getName())){
			PlaceholderST st =ShortHolderFactory.create(sqlManager.getNc(),format.getName(),exp);
			if(st==null){
				throw new IllegalArgumentException("错误的用法 "+format.getName()+" "+exp.token.toString());
			}
			return st;
		}
		return new SQLPlaceholderST(exp, format, null);
	}

	@Override
	public PlaceholderST createTextOutputSt2(Expression exp, FormatExpression format) {
		disableSyntaxCheck("TextOutputSt2");
		if(format!=null&&ShortHolderFactory.isSupport(format.getName())){
			PlaceholderST st = ShortHolderFactory.create(sqlManager.getNc(),format.getName(),exp);
			if(st==null){
				throw new IllegalArgumentException("错误的用法 "+format.getName()+" "+exp.token.toString());
			}
			return st;
		}
		return new PlaceholderST(exp, format, null);
	}

    /* 内置了优化 */
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
