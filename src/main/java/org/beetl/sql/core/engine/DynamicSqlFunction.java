package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.SQLSource;

public class DynamicSqlFunction  implements Function {
	@Override
	public Object call(Object[] paras, Context ctx) {
		String sqlTemplate = (String)paras[0];
		String key ="auto._gen_" +sqlTemplate;
		
		Map inputParas  = ctx.globalVar;
		if(paras.length==2){
			Map map = (Map)paras[1];
			map.putAll(inputParas);
			inputParas = map ;
		}
		
		SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
		List list = (List)ctx.getGlobal("_paras");
		List mapping = (List)ctx.getGlobal("_mapping");
		
		SQLSource source = sm.getSqlLoader().getSQL(key);
		if(source==null){
			source = new SQLSource(key,sqlTemplate);
			sm.getSqlLoader().addSQL(key, source);		
		}
		
		SQLResult result=sm.getSQLResult(source, inputParas);
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
