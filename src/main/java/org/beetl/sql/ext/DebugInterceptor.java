package org.beetl.sql.ext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.EnumKit;

/**
 * Debug重新美化版本
 * @author darren xiandafu
 * @version 2016年8月25日
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
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator", "\n");
		sb.append("┏━━━━━ Debug [").append(this.getSqlId(sqlId)).append("] ━━━").append(lineSeparator)
		.append("┣ SQL：\t " + ctx.getSql().replaceAll("--.*", "").replaceAll("\\s+", " ")).append(lineSeparator)
		.append("┣ 参数：\t " + formatParas(ctx.getParas())).append(lineSeparator);
		RuntimeException ex = new  RuntimeException();
		StackTraceElement[] traces = ex.getStackTrace();
		boolean found = false ;
		for(StackTraceElement tr:traces){
			if(!found&&tr.getClassName().indexOf("SQLManager")!=-1){
				found = true ;	
			}
			if(found&&!tr.getClassName().startsWith("org.beetl.sql.core")&&!tr.getClassName().startsWith("com.sun")){
				String className = tr.getClassName();
				String mehodName = tr.getMethodName();
				int line = tr.getLineNumber();
				sb.append("┣ 位置：\t "+className+"."+mehodName+"("+tr.getFileName()+":"+line+")"+lineSeparator);
				break ;
			}
		}
		ctx.put("logs", sb);
	}

	@Override
	public void after(InterceptorContext ctx) {
		long time = System.currentTimeMillis();
		long start = (Long)ctx.get("debug.time");
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb =(StringBuilder) ctx.get("logs");
		sb.append("┣ 时间：\t "+(time-start)+"ms").append(lineSeparator);
		if(ctx.isUpdate()){
			sb.append("┣ 更新：\t [");
			if(ctx.getResult().getClass().isArray()){
				int[] ret = (int[])ctx.getResult();
				for(int i=0;i<ret.length;i++){
					if(i>0) sb.append(",");
					sb.append(ret[i]);
				}
			}else{
				sb.append(ctx.getResult());
			}
			sb.append("]").append(lineSeparator);
		}else{
			if(ctx.getResult() instanceof Collection){
				sb.append("┣ 结果：\t [").append(((Collection)ctx.getResult()).size()).append("]").append(lineSeparator);
			}else{
				sb.append("┣ 结果：\t [").append(ctx.getResult()).append("]").append(lineSeparator);
			}
			
		}
		sb.append("┗━━━━━ Debug [").append(this.getSqlId(ctx.getSqlId())).append("] ━━━").append(lineSeparator);
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
	
	protected List<String> formatParas(List<SQLParameter> list){
		List<String> data = new ArrayList<String>(list.size());
		for( SQLParameter para:list){
			Object obj = para.value;
			if(obj==null){
				data.add(null);
			}else if(obj instanceof String){
				String str = (String)obj;
				if(str.length()>20){
					data.add(str.substring(0, 20)+"...("+str.length()+")");
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
	
	protected String getSqlId(String sqlId){
		if(sqlId.length()>50){
			sqlId = sqlId.substring(0,50);
			sqlId= sqlId+"...";
		}
		return sqlId;
	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb =(StringBuilder) ctx.get("logs");
		sb.append("┗━━━━━ Debug [ ERROR:").append(ex!=null?ex.getMessage():"").append("] ━━━").append(lineSeparator);
		println(sb.toString());
		
	}

}