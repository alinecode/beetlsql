package org.beetl.sql.core.orm;

import java.util.LinkedList;
import java.util.List;

import org.beetl.core.Context;
import org.beetl.core.Function;
/**
 * 记录映射关系
 * <pre>
 * one2manyEntity("sys_role","role_id","roles");
 * </pre>
 * @author xiandafu
 *
 */
public class One2ManyEntityFunction implements Function {

	@Override
	public Object call(Object[] paras, Context ctx) {
		String tableName = (String)paras[0];
		Object o = paras[1];
		String tailAttrName = (String)paras[2];
		MappingKey key = null;
		if(o instanceof String){
			key = new SimpleMappingKey((String)o);
		}else{
			throw new UnsupportedOperationException("comming soon");
		}
		List<Mapping> list =(List<Mapping>) ctx.getGlobal("_mappping");
		if(list==null){
			list = new LinkedList<Mapping>();
		}
		MappingEntity mappingEntity = new MappingEntity();
		mappingEntity.setSingle(false);
		mappingEntity.setMappingKey(key);
		mappingEntity.setTailAttrName(tailAttrName);
		return null;
	}

}
