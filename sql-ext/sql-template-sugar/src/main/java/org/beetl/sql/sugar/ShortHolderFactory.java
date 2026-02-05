package org.beetl.sql.sugar;

import org.beetl.core.statement.*;
import org.beetl.sql.clazz.NameConversion;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 占位符语法糖，当占位符里的变量不存在或者为null，此占位符不输出。
 * 当不为null时候，其输出格式取决于格式化函数
 */
public class ShortHolderFactory {
	static Set<String> shortSet = new HashSet<>();
	static {
		shortSet.add("and");
		shortSet.add("or");
		shortSet.add("asc");
		shortSet.add("desc");
		shortSet.add("set");

	}


	public static boolean isSupport(String formatName){
		return shortSet.contains(formatName);
	}

	public static PlaceholderST create(NameConversion nc,String formatName,Expression expression){
		if(formatName.equals("and")||formatName.equals("or")) {
			return createAndOr(nc,"where",formatName,expression);
		}else if(formatName.equals("set")){
			return createSet(nc,"set",",",expression);
		}else if(formatName.equals("asc")||formatName.equals("desc")){
			return createOrder("order by",formatName,expression);
		}else{
			// 不可能发生
			throw new IllegalArgumentException(formatName);
		}

	}

	protected  static SQLPlaceholderSTShort createSet(NameConversion nc,String check,String prefix,Expression expression){

		VarRef ref =deepFind(expression);
		if(ref==null){
			return null;
		}
		//TODO 修改了原来表达式，改成安全输出
		ref.hasSafe = true;
//		VarRef newRef = new VarRef(ref.attributes,true,null,ref.token);
//		newRef.setVarIndex(ref.getVarIndex());
		FunctionExpression condition = new FunctionExpression("isNotEmpty", new Expression[]{ref}, null, false, null, ref.token);
		String  col = nc.getColName(ref.token.text)+"=";
		SQLPlaceholderSTShort newSt = new SQLPlaceholderSTShort(condition,check,prefix,col,expression);
		return newSt;

	}
	protected  static OrderByPlaceholderSTShort createOrder(String prefix,String formatName,Expression expression){
		if(!(expression instanceof  VarRef)){
			return null;
		}
		VarRef ref =( VarRef)expression;
		VarRef newRef = new VarRef(ref.attributes,true,null,ref.token);
		newRef.setVarIndex(ref.getVarIndex());

		FunctionExpression condition = new FunctionExpression("isNotEmpty", new Expression[]{newRef}, null, false, null, ref.token);

		OrderByPlaceholderSTShort newSt = new OrderByPlaceholderSTShort(condition,prefix,formatName,expression);
		return newSt;

	}


	/**
	 * where 1=1 #{b,and} 转化为 where 1=1 #{isNotEmpty(b!)?('and b='+b)},即 where 1=1 and b=？
	 * @param nc
	 * @param check
	 * @param prefix
	 * @param expression
	 * @return
	 */
	protected  static SQLPlaceholderSTShort createAndOr(NameConversion nc,String check,String prefix,Expression expression){
		VarRef ref = null;
		String sqlMode = null;
		String colExpress = null;
		Expression realExpression = null;
		if (expression instanceof CompareExpression) {
			CompareExpression compareExpression = (CompareExpression) expression;
			Expression left = compareExpression.a;
			Expression right = compareExpression.b;
			int mode = compareExpression.compareMode;
			sqlMode = getSQLModeString(mode);
			//检测条件
			if (left instanceof Literal) {
				VarRef targetRef = deepFind(right);
				if(targetRef==null){
					return null;
				}
				ref = new VarRef(targetRef.attributes,true,null,targetRef.token);
				ref.setVarIndex(targetRef.getVarIndex());
				colExpress = left.toString()+sqlMode;
				realExpression = right;
			}else if(right instanceof Literal){
				VarRef targetRef = deepFind(left);
				if(targetRef==null){
					return null;
				}
				ref = new VarRef(targetRef.attributes,true,null,targetRef.token);
				ref.setVarIndex(targetRef.getVarIndex());
				colExpress = right.toString()+sqlMode;
				realExpression = left;
			}
		}else {
			ref = deepFind(expression);
			realExpression = expression;
			if(ref!=null){
				int mode = CompareExpression.EQUAL;
				sqlMode = getSQLModeString(mode);
				colExpress = nc.getColName(ref.token.text)+sqlMode;

			}

		}

		FunctionExpression condition = new FunctionExpression("isNotEmpty", new Expression[]{ref}, null, false, null, ref.token);
		SQLPlaceholderSTShort newSt = new SQLPlaceholderSTShort(condition,check,prefix,colExpress,realExpression);
		return newSt;

	}

	protected  static String getSQLModeString(int mode){
		switch (mode){
			case CompareExpression.EQUAL:return "=";
			case CompareExpression.LARGE:return ">";
			case CompareExpression.LESS:return "<";
			case CompareExpression.LARGE_EQUAL:return ">=";
			case CompareExpression.LESS_EQUAL:return "<=";
			case CompareExpression.NOT_EQUAL:return "!=";
		}
		throw new IllegalArgumentException("not support "+mode);
	}







	protected static VarRef deepFind(Expression expression) {
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





}
