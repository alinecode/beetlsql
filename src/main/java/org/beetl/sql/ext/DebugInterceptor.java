package org.beetl.sql.ext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.kit.EnumKit;

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
		RuntimeException ex = new  RuntimeException();
		StackTraceElement[] traces = ex.getStackTrace();
		boolean found = false ;
		for(StackTraceElement tr:traces){
			if(!found&&tr.getClassName().indexOf("SQLManager")!=-1){
				found = true ;
			}
			
			if(found&&!tr.getClassName().startsWith("org.beetl.sql.core")&&!tr.getClassName().startsWith("com.sun")){
				//startwith("com.sun"),proxy call,please refer to MapperJava Proxy since beetlsql 2.0
				//found 
				String className = tr.getClassName();
				String mehodName = tr.getMethodName();
				int line = tr.getLineNumber();
				println("location:"+className+"."+mehodName+" "+line);
				break ;
			}
		}
		
		return ;
		

	}

	@Override
	public void after(InterceptorContext ctx) {
		long time = System.currentTimeMillis();
		long start = (Long)ctx.get("debug.time");
		
		StringBuilder sb = new StringBuilder();
		sb.append("======DebugInterceptor After======\n")
			.append("sqlId : " + ctx.getSqlId()).append("\n")
			.append("execution time : "+(time-start)+"ms").append("\n");
		
		if(ctx.isUpdate()){
			sb.append("成功更新[");
			if(ctx.getResult().getClass().isArray()){
				int[] ret = (int[])ctx.getResult();
				for(int i=0;i<ret.length;i++){
					sb.append(ret[i]);
					if(i!=ret.length-1){
						sb.append(",");
					}
				}
				
			}else{
				sb.append(ctx.getResult());
			}
			sb.append("]");
		}else{
			sb.append("成功返回[").append(ctx.getResult()).append("]");
		}
		sb.append("\n");
		println(sb.toString());

	}
	
	protected void print(String sqlId,String sql,List<Object> paras){
		StringBuilder sb = new StringBuilder();
		sb.append("======DebugInterceptor Before======\n")
			.append("sqlId : "+sqlId).append("\n")
			.append("sql ： " + sql)
			.append("\nparas : " + formatParas(paras));
		println(sb.toString());
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
	
	protected List<String> formatParas(List<Object> list){
		List<String> data = new ArrayList<String>(list.size());
		for(Object obj:list){
			if(obj==null){
				data.add(null);
			}else if(obj instanceof String){
				String str = (String)obj;
				if(str.length()>20){
					data.add(str.substring(0, 20));
				}else{
					data.add(str);
				}
			}else if(obj instanceof Date){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				data.add(sdf.format((Date)obj));
			}else if(obj instanceof Enum){
				Object value = EnumKit.getValueByEnum(obj);
				data.add(String.valueOf(value));
			}else {
				data.add(obj.toString());
			}
		}
		return data;
	}
	
	protected void println(String str){
		System.out.println(str);
	}

}
