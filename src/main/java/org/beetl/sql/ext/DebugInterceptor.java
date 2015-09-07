package org.beetl.sql.ext;

import java.util.List;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;

/** 控制台输出sql
 * @author joelli
 *
 */
public class DebugInterceptor implements Interceptor {

	List<String> includes = null;
	public DebugInterceptor(){
	}
	
	public DebugInterceptor(List<String> includes){
		this.includes = includes;
	}
	@Override
	public void before(InterceptorContext ctx) {
		String sqlId = ctx.getSqlId();
		if(this.isDebugEanble(sqlId)){
			ctx.put("debug.time", System.currentTimeMillis());
		}
		print(sqlId,ctx.getSql(),ctx.getParas());
		return ;
		

	}

	@Override
	public void after(InterceptorContext ctx) {
		long time = System.currentTimeMillis();
		long start = (Long)ctx.get("debug.time");
		
		StringBuilder sb = new StringBuilder();
		sb.append("======DebugInterceptor After======\n")
			.append("sqlId : " + ctx.getSqlId()).append("\n")
			.append("execution time : "+(time-start)+"ms").append("\n")
			.append("result : success");
		System.out.println(sb.toString());

	}
	
	protected void print(String sqlId,String sql,List<Object> paras){
		StringBuilder sb = new StringBuilder();
		sb.append("======DebugInterceptor Before======\n")
			.append("sqlId : "+sqlId).append("\n")
			.append("sql ： " + sql)
			.append("paras : " + paras);
		System.out.println(sb.toString());
	}
	
	protected boolean isDebugEanble(String sqlId){
		if(this.includes==null) return true;
		for(String id:includes){
			if(sqlId.startsWith(id)){
				return true;
			}
		}
		
		return false;
	}

}
