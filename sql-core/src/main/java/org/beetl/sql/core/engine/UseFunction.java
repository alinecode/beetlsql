package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.template.BeetlTemplateContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用于使用另外一个sql片段 #use("xxxx")#
 * @author xiandafu
 *
 */
public class UseFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		String id = (String) paras[0];
		Map inputParas = ctx.globalVar;
		if (paras.length == 2) {
			//use 方法允许提供额外的参数
			Map map = (Map) paras[1];
			map.putAll(inputParas);
			inputParas = map;
		}

		// 保留，免得被覆盖
		List list = (List) ctx.getGlobal("_paras");
		ExecuteContext executeContext = (ExecuteContext)ctx.getGlobal("_executeContext");
		SqlId originalId = executeContext.sqlSource.getId();
		SqlId theId = originalId.sibling(id);
		SQLResult result = executeContext.sqlManager.getSQLResult(theId, inputParas, new BeetlTemplateContext(ctx));
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
