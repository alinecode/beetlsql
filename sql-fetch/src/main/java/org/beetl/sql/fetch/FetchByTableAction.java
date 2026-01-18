package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.fetch.annotation.FetchByTable;
import org.beetl.sql.fetch.annotation.FetchSql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @see org.beetl.sql.fetch.annotation.FetchByTable
 */
public class FetchByTableAction extends  AbstractFetchAction {
	@Override
	public void execute(ExecuteContext ctx, List list) {
		boolean isSingle = true;
		//目标对象
		Class targetType = this.originProperty.getProp().getPropertyType();
		String tagetTable
			= ctx.sqlManager.getNc().getTableName(targetType);
		String targetTypeIdAttr = findIdProperty(targetType,ctx.sqlManager).getProp().getName();
		FetchByTable fetchAnnotation = (FetchByTable) annotation;
		Class intermediateClass = fetchAnnotation.tableClass();
		String intermediateTable = ctx.sqlManager.getNc().getTableName(intermediateClass);
		String fkAttr = fetchAnnotation.fkAttr();
		PropertyDescriptorWrap beanIdProperty = findIdProperty(owner,ctx.sqlManager);
		//TODO,改成一次加载所有数据，比如 where i.kAttr in (1,2,3)
		String sql = "select * from "+intermediateTable+" i left join "+tagetTable+" t on i."+fkAttr+"=t."+targetTypeIdAttr
			+" where i."+fkAttr+"=?";
		for(int i=0;i<list.size();i++){

			Object obj = list.get(i);
			Object cached  = queryFromCache(ctx.sqlManager,obj);
			//检测缓存
			if(cached!=null){
				list.remove(i);
				//使用缓存对象代替，不需要后续操作数据库，也避免循环引用
				list.add(i,cached);
				if(containAttribute(cached,originProperty.getProp().getName())){
					//对象的字段已经被fetch过了
					continue;
				}
				obj = cached;
			}else{
				//缓存自己，也避免未来循环引用
				addCached(ctx.sqlManager,obj);
			}
			//执行数据库查询操作
			List ret = ctx.sqlManager.execute(new SQLReady(sql,beanIdProperty.getValue(obj)),targetType);
			addAttribute(obj,originProperty.getProp().getName());
			originProperty.setValue(obj,ret);
		}

	}
}
