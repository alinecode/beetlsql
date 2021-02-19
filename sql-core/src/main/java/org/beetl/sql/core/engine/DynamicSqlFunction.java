package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.*;
import org.beetl.sql.core.engine.template.BeetlTemplateContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 类似use，但是动态提供一个sql
 * <pre>{@code
 * queryUsers
 * ===
 * -- @ var sql = "id=#{xxx}";
 * select #{page("*")} from user where 1=1 and #{db.dynamicSql(sql,{xxx:1\})}
 * }</pre>
 *
 * 这里的sql可以是在md文件中定义的，也可以是外部传入的
 * @author xiandafu
 * @see UseFunction
 * @see GlobalUseFunction
 */
public class DynamicSqlFunction implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {

		List list = (List) ctx.getGlobal("_paras");
		ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");
		SQLManager sqlManager = executeContext.sqlManager;

		String sqlTemplate = (String) paras[0];
		SqlId id = sqlManager.getSqlIdFactory().buildSql(sqlTemplate);
		SQLSource source = executeContext.sqlManager.getSqlLoader().queryAutoSQL(id);
		if (source == null) {
			source = new SQLSource(id, sqlTemplate);
			sqlManager.getSqlLoader().addSQL(id, source);
		}


		//包含所有参数以及新增参数
		Map inputParas = ctx.globalVar;
		if (paras.length == 2) {
			Map map = (Map) paras[1];
			map.putAll(inputParas);
			inputParas = map;
		}


		SQLResult result = sqlManager.getSQLResult(id, inputParas, new BeetlTemplateContext(ctx));
		//追加参数
		list.addAll(result.jdbcPara);
		ctx.set("_paras", list);
		//输出sql部分
		try {
			ctx.byteWriter.writeString(result.jdbcSql);
		} catch (IOException e) {

		}
		return null;

	}
}
