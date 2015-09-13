package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public class UseFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		String id = (String)paras[0];
		SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
		// 保留，免得被覆盖
		List list = (List)ctx.getGlobal("_paras");
		String file = this.getParentId(ctx);
		SQLResult result = sm.getSQLResult(file+"."+id, ctx.globalVar,(String)ctx.getGlobal("_id"));
		list.addAll(result.jdbcPara);
		ctx.set("_paras", list);
		try {
			ctx.byteWriter.writeString( result.jdbcSql);
		} catch (IOException e) {
			
		}
		return null;
	}
	
	private String getParentId( Context ctx){
		String id = (String)ctx.getGlobal("_id");
		int index = id.lastIndexOf(".");
		String file = id.substring(0, index);
		return file;
	}

}
