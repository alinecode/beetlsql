package org.beetl.sql.core.engine;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.orm.MappingEntity;
import org.beetl.sql.core.orm.MappingFunctionHelper;
/**
 * 用于全局#gloabUse("other.xxxx")#
 * @author xiandafu
 *
 */
public class UseFunction implements Function {

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
		
		String file = this.getParentId(ctx);
		SQLResult result = sm.getSQLResult(file+"."+id, inputParas,ctx);
		
		//追加参数
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
