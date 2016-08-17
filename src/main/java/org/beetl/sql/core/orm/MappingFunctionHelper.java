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

	
	
	protected void parse(boolean single,boolean isClass,Object[] paras,Context ctx){
		Map<String,String> mapkey = (Map<String,String>)paras[0];
		String className = (String)paras[1];
	
		String tailAttrName = (String)paras[2];
		
		List<MappingEntity> list =(List<MappingEntity>) ctx.getGlobal("_mappping");
		if(list==null){
			list = new LinkedList<MappingEntity>();
		}
		MappingEntity mappingEntity = new MappingEntity();
		mappingEntity.setSingle(single);
		mappingEntity.setMapkey(mapkey);
		mappingEntity.setTailAttrName(tailAttrName);
		mappingEntity.setTarget(className);
		mappingEntity.setClassMapping(isClass);
		list.add(mappingEntity);
		ctx.globalVar.put("_mapping", list);
	}
	
	

}
