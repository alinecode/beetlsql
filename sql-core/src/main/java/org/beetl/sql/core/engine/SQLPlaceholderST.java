package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.statement.*;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.db.DBStyle;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 占位符输出，一般输出？，并记录变量，特殊情况可以直接输出文本
 * @author xiandafu
 */
public class SQLPlaceholderST extends PlaceholderST {

	/**
	 *  这些函数调用总是返回函数结果而不是一个sql占位符号“?”
	 */
	public static final Set<String> textFunList = new HashSet<String>();

	static {
		textFunList.add("text");
		textFunList.add("use");
		textFunList.add("globalUse");
		textFunList.add("join");
		textFunList.add("page");

	}

	public SQLPlaceholderST(Expression exp, FormatExpression format, GrammarToken token) {
		super(exp, format, token);


	}

	//Override
	@Override
	public final void execute(Context ctx) {
		try {
			Object value = expression.evaluate(ctx);
			ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");
			DBStyle dbStyle = executeContext.sqlManager.getDbStyle();
			if(!dbStyle.preparedStatementSupport()){
				/* 这种情况，拼接sql字符串，但dbStyle.wrapStatementValue实现方法应该要防止sql注入，
				 或者更底层的Connection Pool防止 */
				String sqlAppend = dbStyle.wrapStatementValue(value);
				List list = (List) ctx.getGlobal("_paras");
				int type = SQLParameter.NAME_EXPRESSION;
				SQLParameter sqlPara = new SQLParameter(expression.token.text, value,type);
				list.add(sqlPara);

				ctx.byteWriter.writeString(sqlAppend);
				return ;
			}

			int jdbcType = 0;
			if (format != null) {
				String formatName = format.token.text;
				if (formatName.startsWith("typeof")) {
					//特殊的format，告诉此对象应该作为jdbc类型
					String str = formatName.substring(6).toLowerCase();
					str = StringKit.toLowerCaseFirstOne(str);
					Integer expectJdbcType = JavaType.jdbcTypeNames.get(str);
					if (expectJdbcType == null) {
						BeetlException be = new BeetlException(BeetlException.FORMAT_NOT_FOUND,
								formatName + "是用来指示jdbc类型，并不存在，请检查java.sql.Type");
						be.pushToken(format.token);
						throw be;
					}
					jdbcType = expectJdbcType;
				} else if (formatName.equals("jdbc")) {
					Integer expectJdbcType = (Integer) format.evaluateValue(value, ctx);
					if (expectJdbcType == null) {
						BeetlException be = new BeetlException(BeetlException.ERROR,
								formatName + "是用来指示jdbc类型，并不存在，请检查java.sql.Type");
						be.pushToken(format.token);
						throw be;
					}
					jdbcType = expectJdbcType;

				} else {
					value = format.evaluateValue(value, ctx);
				}

			}

			if (expression instanceof FunctionExpression) {
				//db 开头或者内置的方法直接输出
				FunctionExpression fun = (FunctionExpression) expression;
				String funName = fun.token.text;
				if (funName.startsWith("db")) {
					ctx.byteWriter.writeString(value != null ? value.toString() : "");
					return;
				} else if (textFunList.contains(funName)) {
					ctx.byteWriter.writeString(value != null ? value.toString() : "");
					return;
				}
			}
			int type = SQLParameter.NAME_EXPRESSION;
			ctx.byteWriter.writeString("?");
			List list = (List) ctx.getGlobal("_paras");
			SQLParameter sqlPara = new SQLParameter(expression.token.text, value, type);
			sqlPara.setJdbcType(jdbcType);
			list.add(sqlPara);


		} catch (IOException e) {
			BeetlException be = new BeetlException(BeetlException.CLIENT_IO_ERROR_ERROR, e.getMessage(), e);
			be.pushToken(this.token);
			throw be;
		}

	}


}