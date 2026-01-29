package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.engine.GrammarCreator;
import org.beetl.core.parser.BeetlParser;
import org.beetl.core.statement.*;
import org.beetl.sql.core.SQLManager;

import java.io.IOException;
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
		if(format!=null&&format.getName().equals("and")){
			//特殊处理占位符
			return  newPlaceholderStatement(exp, format);
		}
		return new SQLPlaceholderST(exp, format, null);
	}

	@Override
	public PlaceholderST createTextOutputSt2(Expression exp, FormatExpression format) {
		disableSyntaxCheck("TextOutputSt2");
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


	/**
	 * 如果格式化函数里有and，or，或者set，则转化内部表达式，比如
	 * where 1=1 #{b,and} 转化为 where 1=1 #{isNotEmpty(b!)?('and b='+b)},即 where 1=1 and b=？
	 * @param expression
	 * @param format
	 * @return
	 */
	protected SQLPlaceholderST newPlaceholderStatement(Expression expression, FormatExpression format) {


		if (expression instanceof CompareExpression) {
			CompareExpression compareExpression = (CompareExpression) expression;
			Expression left = compareExpression.a;
			Expression right = compareExpression.b;
			int mode = compareExpression.compareMode;
			//检测条件

			if (left instanceof Literal ) {
				VarRef ref = deepFind(right);
				ref.hasSafe =true;
				if(ref!=null){
					return newPlaceholderStatement(right,ref,(Literal) left,format.getName(),mode);
				}

			}else if(right instanceof Literal){
				VarRef ref = deepFind(left);
				ref.hasSafe =true;
				if(ref!=null){
					return newPlaceholderStatement(left,ref,(Literal) right,format.getName(),mode);
				}

			}
		}else {
			VarRef  ref = deepFind(expression);

			if(ref!=null){
				int mode = CompareExpression.EQUAL;
				String formatName = "and";
				Literal literal = new Literal(sqlManager.getNc().getColName(ref.firstToken.text),ref.token);
				return newPlaceholderStatement(expression,ref,literal,formatName,mode);
			}

		}
		//其他情况
		return new SQLPlaceholderST(expression, format, null);

	}
	/*重写表达式，将原来的简单表达式转化为 b=='b' -->  isNotEmpty(b)?('and b='+b)*/
	protected  SQLPlaceholderSTShort newPlaceholderStatement(Expression expression,VarRef ref,Literal literal,String formatName,int mode){

		//改成安全输出
		VarRef newRef = new VarRef(ref.attributes,true,null,ref.token);
		newRef.setVarIndex(ref.getVarIndex());

		FunctionExpression condition = new FunctionExpression("isNotEmpty", new Expression[]{newRef}, null, false, null, ref.token);


		Literal colLiteral= new Literal(formatName+" "+literal+"=",literal.token);
		ArthExpression arthExpression = new ArthExpression(colLiteral,expression,ArthExpression.PLUS,ref.token);
		//isEmpty(b)?null: ('and b='+b)
		SQLPlaceholderSTShort  newSt = new SQLPlaceholderSTShort(condition,colLiteral.toString(),expression,null,expression.token);
		return newSt;
	}


	protected VarRef deepFind(Expression expression) {
		if(expression instanceof  VarRef){
			return (VarRef)expression;
		}

		Field[] fields =expression.getClass().getFields();
		List<Expression> expressionList = new ArrayList<>();
		for(Field field:fields){
			field.setAccessible(true);
			Object value = null;
			try{
				value = field.get(expression);
			}catch (Exception re){
				throw new RuntimeException(re);
			}

			if(value instanceof Expression){
				if(value instanceof VarRef){
					return (VarRef)value;
				}else{
					expressionList.add((Expression)value);
				}
			}else if(value.getClass().isArray()&&value.getClass().getComponentType()==Expression.class){
				Expression[] expressions = (Expression[])value;
				if(expressions.length>0&&expressions[0] instanceof VarRef){
					return (VarRef)expressions[0];
				}
				expressionList.addAll(Arrays.asList(expressions));
			}

		}
		for(Expression child:expressionList){
			VarRef varRef = deepFind(child);
			if(varRef!=null){
				return varRef;
			}
		}
		return null;

	}

	public static class SQLPlaceholderSTShort extends SQLPlaceholderST{

		Expression conditionExpress;
		String str;
		public SQLPlaceholderSTShort(Expression conditionExpress,String str,Expression exp, FormatExpression format, GrammarToken token) {
			super(exp, format, token);
			this.conditionExpress = conditionExpress;
			this.str = str;
		}
		@Override
		public  void execute(Context ctx) {
			Boolean b = (Boolean)conditionExpress.evaluate(ctx);
			if(!b){
				return ;
			}
			try {
				ctx.byteWriter.writeString(str);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			super.execute(ctx);
		}
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
