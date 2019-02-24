package org.beetl.sql.core.orm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.beetl.core.Context;
import org.beetl.sql.core.SQLResultListener;
/**
 * 记录映射关系
 *
 * @author xiandafu
 *
 */
public class MappingFunctionHelper  {

	
	
	
	protected void parse(boolean single,boolean lazy,Object[] paras,Context ctx){
//		if(ctx.getGlobal("_page")!=null){
//			//翻页查询,为什么翻页查询不做映射？
//			return ;
//		}
		Map<String,String> mapkey = (Map<String,String>)paras[0];
		String className = null;
		String sqlId = null;
		String tailName = null;
		Map<String,Object> queryParas = null;
		
		Object last = paras[paras.length-1];
		int len = paras.length;
		if(last instanceof Map){
			queryParas =  (Map<String,Object>)last;
			tailName = (String)queryParas.get("alias");
			queryParas.remove("alias");
			if(queryParas.size()==0){
				queryParas = null;
			}
			//TODO fitler&Order
			len = len-1;
	
		}
		
				
		if(len>=3){
			//使用了sqlid
			className = (String)paras[2];
			sqlId = (String)paras[1];
		}else{
			className = (String)paras[1];
			
		}
	
			
		MappingEntity mappingEntity = null;
		if(lazy){
			 mappingEntity = new LazyMappingEntity();
		}else{
			 mappingEntity = new MappingEntity();
		}
			
		mappingEntity.setSingle(single);
		mappingEntity.setMapkey(mapkey);
		mappingEntity.setTarget(className);
		mappingEntity.setSqlId(sqlId);
		mappingEntity.setTailName(tailName);
		mappingEntity.setQueryParas(queryParas);
		
		addMapping(ctx,mappingEntity);
	}
	
//	public static final String MAPPING="_mapping";
//	public static void  merge(List<MappingEntity> before,List<MappingEntity> after,Context ctx){
//		if(before==null&&after==null) {
//			return ;
//		}else if(before==null) {
//			ctx.set(MAPPING, after);
//		}
//		
//	}
	
	public static void addMapping(Context ctx,MappingEntity mappingConfig) {
		List<SQLResultListener> ls =  (List<SQLResultListener>)ctx.getGlobal("_listener");
		if(ls==null) {
			ls = new ArrayList<SQLResultListener> ();
			ORMSQLResultListener orm = new ORMSQLResultListener();
			orm.getMapingEntrys().add(mappingConfig);
			ctx.set("_listener", ls);
			return ;
		}else {
			for(SQLResultListener l:ls) {
				if(l instanceof ORMSQLResultListener) {
					((ORMSQLResultListener)l).getMapingEntrys().add(mappingConfig);
					break;
				}
			}
		}
	}

}
