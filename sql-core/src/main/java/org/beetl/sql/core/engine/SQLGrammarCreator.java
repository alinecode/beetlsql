package org.beetl.sql.core.engine;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.statement.*;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.sugar.SQLPlaceholderSTShort;
import org.beetl.sql.core.engine.sugar.ShortHolderFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对sql模板语法进行定制，比如占位符输出"?"而不是实际内容
 * @author xiandafu
 */
public class SQLGrammarCreator extends GrammarCreator {
	SQLManager sqlManager;
	public SQLGrammarCreator(SQLManager sqlManager) {
		super();
		this.sqlManager = sqlManager;
	}
	@Override
    public PlaceholderST createTextOutputSt(Expression exp, FormatExpression format) {
		disableSyntaxCheck("TextOutputSt");
		if(format!=null&&ShortHolderFactory.isSupport(format.getName())){
			PlaceholderST st = ShortHolderFactory.create(sqlManager.getNc(),format.getName(),exp);
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

	@Override
	public FunctionExpression createFunction(String name, Expression[] exps, VarAttribute[] vas, boolean hasSafe,
			Expression safeExp, GrammarToken token) {
		disableSyntaxCheck("Function");
		return new SqlFunctionExpression(name, exps, vas, hasSafe, safeExp, token);
	}

	@Override
	public FunctionExpression createFunctionExp(String name, Expression[] exps, VarAttribute[] vas, boolean hasSafe,
			Expression safeExp, GrammarToken token) {
		disableSyntaxCheck("FunctionExp");
		return new SqlFunctionExpression(name, exps, vas, hasSafe, safeExp, token);
	}





	public static class SqlFunctionExpression extends FunctionExpression{

		public SqlFunctionExpression(String name, Expression[] exps, VarAttribute[] vas, boolean hasSafe,
				Expression safeExp, GrammarToken token) {
			super(name, exps, vas, hasSafe, safeExp, token);
		}


		/**
		 * 对未定义的方法，不在从资源里寻找"模板方法"，虽然让md实现beetl方法也不错，但不是模板语言
		 * 还是在java里实现方法最好
		 * @param gt
		 * @param name
		 * @return
		 */
		@Override
        protected Resource getResource(GroupTemplate gt, String name) {
			return null;
		}
	}


}
