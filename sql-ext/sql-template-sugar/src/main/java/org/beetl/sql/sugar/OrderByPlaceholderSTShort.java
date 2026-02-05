package org.beetl.sql.sugar;

import org.beetl.core.Context;
import org.beetl.core.statement.Expression;
import org.beetl.core.statement.PlaceholderST;

import java.io.IOException;

/**
 * 一个简化的表达式输出, 用于处理order by #{b,desc},#{c,asc} 转化成 order by a asc,
 */
public  class OrderByPlaceholderSTShort extends PlaceholderST {

	Expression conditionExpress;
	String colExpress;
	String prefix;
	String check;

	/**
	 *
	 * @param conditionExpress 条件表达式，比如${b}中的表达式b，变成isNotEmpty(b!)
	 * @param prefix 如果不存在check，则增加此pefix，如sql语句结尾没有where，则加上“，”
	 * @param colExpress 列字符串，比如 'b'=
	 * @param exp  表达式
	 */
	public OrderByPlaceholderSTShort(Expression conditionExpress,String prefix,String colExpress,Expression exp) {
		super(exp, null, exp.token);
		this.conditionExpress = conditionExpress;
		this.colExpress = colExpress;
		this.prefix = prefix;

	}
	@Override
	public  void execute(Context ctx) {
		Boolean b = (Boolean)conditionExpress.evaluate(ctx);
		if(!b){
			return ;
		}
		try {

			ctx.byteWriter.writeString(prefix+" ");
			super.execute(ctx);
			ctx.byteWriter.writeString(" "+colExpress);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
