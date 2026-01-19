package org.beetl.sql.fetch;

import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.DynamicFetchEnableOnFunction;
import org.beetl.sql.fetch.annotation.FetchByTable;
import org.beetl.sql.fetch.annotation.FetchMany;
import org.beetl.sql.fetch.annotation.FetchSql;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @see org.beetl.sql.fetch.annotation.FetchByTable
 */
public class FetchByTableAction extends  AbstractFetchAction {

	protected Class targetType;

	@Override
	public void init(Class owner, Class target, Annotation config, PropertyDescriptorWrap originProperty){
		super.init(owner, target, config, originProperty);
		FetchByTable fetchByTable = (FetchByTable)config;
		enableOn = fetchByTable.enableOn();
		Class targetListClass= this.originProperty.getProp().getPropertyType();
		Type type = originProperty.getProp().getReadMethod().getGenericReturnType();
		if(!List.class.isAssignableFrom(targetListClass)){
			throw new IllegalStateException("Many2Many 类型应该是List");
		}

		targetType = this.getCollectionType(type);

	}
	@Override
	public void execute(ExecuteContext ctx, List list) {
		if(StringKit.isNotBlank(enableOn)){
			Object v = ctx.getContextPara(enableOn);
			if(v!= DynamicFetchEnableOnFunction.value){
				return ;
			}
		}

		//目标对象
		String tagetTable
			= ctx.sqlManager.getNc().getTableName(targetType);

		String targetTypeIdAttr = findIdProperty(targetType,ctx.sqlManager).getProp().getName();
		String targetTypeIdColName = ctx.sqlManager.getNc().getColName(targetType,targetTypeIdAttr);
		FetchByTable fetchAnnotation = (FetchByTable) annotation;
		Class intermediateClass = fetchAnnotation.tableClass();
		String intermediateTable = ctx.sqlManager.getNc().getTableName(intermediateClass);
		String fromAttr = fetchAnnotation.fromAttr();
		String fromAttrColName = ctx.sqlManager.getNc().getColName(intermediateClass,fromAttr);

		String toAttr = fetchAnnotation.toAttr();
		String toAttrColName = ctx.sqlManager.getNc().getColName(intermediateClass,toAttr);
		PropertyDescriptorWrap beanIdProperty = findIdProperty(owner,ctx.sqlManager);
		//TODO,改成一次加载所有数据，比如 where i.kAttr in (1,2,3)
		String sql = "select t.* from "+intermediateTable+" i left join "+tagetTable+" t on i."+toAttrColName+"=t."+targetTypeIdColName
			+" where i."+fromAttrColName+"=?";
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
