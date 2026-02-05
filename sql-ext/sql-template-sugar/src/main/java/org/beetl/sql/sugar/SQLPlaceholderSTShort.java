package org.beetl.sql.sugar;

import org.beetl.core.Context;
import org.beetl.core.statement.Expression;
import org.beetl.sql.core.engine.SQLPlaceholderST;

import java.io.IOException;

/**
 * 一个简化的表达式输出, 用于处理#{b,and} 转化成 'b'=#{b},并更具前面是否有where决定是否加上前缀“，”
 */
public  class SQLPlaceholderSTShort extends SQLPlaceholderST {

	Expression conditionExpress;
	String colExpress;
	String prefix;
	String check;

	/**
	 *
	 * @param conditionExpress 条件表达式，比如#{b}中的表达式b，变成isNotEmpty(b!)
	 * @param check sql语句前缀检测
	 * @param prefix 如果不存在check，则增加此pefix，如sql语句结尾没有where，则加上“，”
	 * @param colExpress 列字符串，比如 'b'=
	 * @param exp  表达式
	 */
	public SQLPlaceholderSTShort(Expression conditionExpress,String check,String prefix,String colExpress,Expression exp) {
		super(exp, null, exp.token);
		this.conditionExpress = conditionExpress;
		this.colExpress = colExpress;
		this.prefix = prefix;
		this.check = check;
	}
	@Override
	public  void execute(Context ctx) {
		Boolean b = (Boolean)conditionExpress.evaluate(ctx);
		if(!b){
			return ;
		}
		try {
			String sql = ctx.byteWriter.toString();

			if(sql.trim().endsWith(" "+check)){
				ctx.byteWriter.writeString(colExpress);
			}else{
				ctx.byteWriter.writeString(prefix+" ");
				ctx.byteWriter.writeString(colExpress);

			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.execute(ctx);
	}
}
