package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SqlId;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *  sql 语句内部可以使用globalUse函数包含另外一个sql文件片段
 * @author xiandafu
 * @see UseFunction
 *
 */
public class GlobalUseFunction implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {
		String idStr = (String) paras[0];
		Map inputParas = ctx.globalVar;
		if (paras.length == 2) {
			Map map = (Map) paras[1];
			map.putAll(inputParas);
			inputParas = map;
		}

		ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");


		SQLManager sm = executeContext.sqlManager;
		SqlId sqlId = SqlId.of(idStr);
		// 保留参数和映射关系，免得被覆盖，TODO，独立一个类来获取所有需要保留的，并执行完毕后merge
		List list = (List) ctx.getGlobal("_paras");
		//执行use函数
		SQLResult result = sm.getSQLResult(sqlId, inputParas);
		//追加参数
		list.addAll(result.jdbcPara);
		ctx.set("_paras", list);
		try {
			ctx.byteWriter.writeString(result.jdbcSql);
		} catch (IOException e) {

		}

		return null;
	}


}
