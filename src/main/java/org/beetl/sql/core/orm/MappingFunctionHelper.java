package org.beetl.sql.core.orm;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
/**
 * 记录映射关系
 *
 * @author xiandafu
 *
 */
public class MappingFunctionHelper  {

	
	
	protected void parse(boolean single,Object[] paras,Context ctx){
		Map<String,String> mapkey = (Map<String,String>)paras[0];
		String className = null;
		String sqlId = null;
		if(paras.length==3){
			
			className = (String)paras[2];
			sqlId = (String)paras[1];
		}else{
			className = (String)paras[1];
			
		}
	
	
		List<MappingEntity> list =(List<MappingEntity>) ctx.getGlobal("_mapping");
		if(list==null){
			list = new LinkedList<MappingEntity>();
		}
		MappingEntity mappingEntity = new MappingEntity();
		mappingEntity.setSingle(single);
		mappingEntity.setMapkey(mapkey);
		mappingEntity.setTarget(className);
		
		mappingEntity.setSqlId(sqlId);
		list.add(mappingEntity);
		ctx.globalVar.put("_mapping", list);
	}
	
	

}
