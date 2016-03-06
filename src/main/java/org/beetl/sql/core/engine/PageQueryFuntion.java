package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;

public class PageQueryFuntion implements Function {

	@Override
	public String call(Object[] paras, Context ctx) {
		Object o = ctx.getGlobal(PageQuery.pageFlag);
		if(o==PageQuery.pageObj){
			return "count(1)";
			
		}else{
			
			if(paras.length==0){
				return "*";
			}else{
				return (String)paras[0];
			}
		}
		
		
	}

}
