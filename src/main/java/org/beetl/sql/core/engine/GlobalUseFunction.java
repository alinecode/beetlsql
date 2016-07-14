package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public class GlobalUseFunction implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {
		String id = (String)paras[0];
		SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
		// 保留，免得被覆盖
		List list = (List)ctx.getGlobal("_paras");
		SQLResult result = sm.getSQLResult(id, ctx.globalVar);
		list.addAll(result.jdbcPara);
		ctx.set("_paras", list);
		try {
			ctx.byteWriter.writeString( result.jdbcSql);
		} catch (IOException e) {
			
		}
		return null;
	}
	

}
