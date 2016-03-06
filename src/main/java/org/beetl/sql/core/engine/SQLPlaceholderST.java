package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.beetl.core.Context;
import org.beetl.core.InferContext;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.statement.Expression;
import org.beetl.core.statement.FormatExpression;
import org.beetl.core.statement.FunctionExpression;
import org.beetl.core.statement.PlaceholderST;
import org.beetl.core.statement.Statement;
import org.beetl.core.statement.Type;

public class SQLPlaceholderST extends Statement
{
	public Expression expression;
	public Type type = null;
	FormatExpression format;
	/**
	 *  这些函数调用总是返回函数结果而不是一个sql占位符号“?”
	 */
	public static final Set<String> textFunList = new HashSet<String>();
	static{
		textFunList.add("text");
		textFunList.add("use");
		textFunList.add("join");
		textFunList.add("page");
		
		
	}

	public SQLPlaceholderST(PlaceholderST st)
	{
		super(st.token);
		this.type = st.type;
		this.expression = st.expression;

	}

	@Override
	public final void execute(Context ctx)
	{
		try{
			Object value = expression.evaluate(ctx);
			if(expression instanceof FunctionExpression){
				//db 开头或者内置的方法直接输出
				FunctionExpression fun = (FunctionExpression)expression;
				String funName = fun.token.text;
				if(funName.startsWith("db")){
					ctx.byteWriter.writeString(value!=null?value.toString():"");
					return ;
				}else if(textFunList.contains(funName)){
					ctx.byteWriter.writeString(value!=null?value.toString():"");
					return ;
				}
			}
			
			ctx.byteWriter.writeString("?");
			List list = (List)ctx.getGlobal("_paras");
			list.add(value);
			
		}
		catch (IOException e)
		{
			BeetlException be = new BeetlException(BeetlException.CLIENT_IO_ERROR_ERROR, e.getMessage(), e);
			be.pushToken(this.token);
			throw be;
		}

	}

	@Override
	public void infer(InferContext inferCtx)
	{
		expression.infer(inferCtx);
		this.type = expression.type;
	}

}