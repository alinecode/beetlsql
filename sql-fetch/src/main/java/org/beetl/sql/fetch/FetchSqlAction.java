package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.fetch.annotation.FetchSql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see FetchSql
 */
public class FetchSqlAction extends  AbstractFetchAction {
	@Override
	public void execute(ExecuteContext ctx, List list) {
		boolean isSingle = true;
		Class classType = this.originProperty.getPropertyType();
		if(List.class.isAssignableFrom(classType)){
			isSingle = false;
		}
		FetchSql fetchSqlAnnotation = (FetchSql) annotation;
		String template = fetchSqlAnnotation.value();
		for(int i=0;i<list.size();i++){

			Object obj = list.get(i);
			Object cached  = queryFromCache(ctx.sqlManager,obj);
			//检测缓存
			if(cached!=null){
				list.remove(i);
				//使用缓存对象代替，不需要操作数据库，也避免循环引用
				list.add(i,cached);
				if(containAttribute(cached,originProperty.getName())){
					//对象的字段已经被fetch过了,TODO,通过null判断不如做一个标记
					continue;
				}
				obj = cached;
			}else{
				//缓存自己，也避免未来循环引用
				addCached(ctx.sqlManager,obj);
			}

			//使用对象自身作为参数
			Map<String,Object> map = new HashMap<>();
			map.put("_root",obj);
			List ret = ctx.sqlManager.execute(template,this.target,map);
			Object value = null;
			if(isSingle){
				value = ret.isEmpty()?null:ret.get(0);
			}else{
				value = ret;
			}
			addAttribute(obj,originProperty.getName());
			BeanKit.setBeanProperty(obj,value,originProperty.getName());

		}

	}
}
