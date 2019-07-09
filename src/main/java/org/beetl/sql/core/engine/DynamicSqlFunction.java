package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DynamicSqlFunction implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {
		String sqlTemplate = (String) paras[0];
		String key = "auto._gen_" + sqlTemplate;

		Map inputParas = ctx.globalVar;
		if (paras.length == 2) {
			Map map = (Map) paras[1];
			map.putAll(inputParas);
			inputParas = map;
		}

		SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
		// 保留参数和映射关系，免得被覆盖
		List list = (List) ctx.getGlobal("_paras");

		SQLSource source = sm.getSqlLoader().getSQL(key);
		if (source == null) {
			source = new SQLSource(key, sqlTemplate);
			sm.getSqlLoader().addSQL(key, source);
		}

		SQLResult result = sm.getSQLResult(source, inputParas);
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
