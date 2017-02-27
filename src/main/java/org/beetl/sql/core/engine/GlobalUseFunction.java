package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public class GlobalUseFunction implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {
		String id = (String)paras[0];
		Map inputParas  = ctx.globalVar;
		if(paras.length==2){
			Map map = (Map)paras[1];
			map.putAll(inputParas);
			inputParas = map ;
		}
		SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
		// 保留，免得被覆盖
		List list = (List)ctx.getGlobal("_paras");
		List mapping = (List)ctx.getGlobal("_mapping");
		SQLResult result = sm.getSQLResult(id, inputParas);
		list.addAll(result.jdbcPara);
		ctx.set("_paras", list);
		if(mapping!=null){
			if(result.mapingEntrys!=null){
				mapping.addAll(result.mapingEntrys);
			}
			
		}else if(result.mapingEntrys!=null){
			ctx.set("_mapping", result.mapingEntrys);
		}
		
		try {
			ctx.byteWriter.writeString( result.jdbcSql);
		} catch (IOException e) {
			
		}
		return null;
	}
	

}
