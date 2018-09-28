package org.beetl.sql.ext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.JavaType;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.EnumKit;
import org.beetl.sql.core.mapper.MapperJavaProxy;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;

/**
 * Debug重新美化版本
 * 
 * @author darren xiandafu
 * @version 2016年8月25日
 *
 */
public class DebugInterceptor implements Interceptor {

	List<String> includes = null;

	static String mapperName = MapperJavaProxy.class.getName();
	static String sqlManager = SQLManager.class.getName();
	static String queryClassName = Query.class.getName();
	static String lambdaQueryName = LambdaQuery.class.getName();
	
	// debug 输入优先输出的类，而不是SQLManager或者是BaseMapper
	String preferredShowClass;

	public DebugInterceptor() {
	}

	public DebugInterceptor(List<String> includes) {
		this.includes = includes;
	}

	public DebugInterceptor(String preferredShowClass) {
		this.preferredShowClass = preferredShowClass;
	}

	public DebugInterceptor(List<String> includes, String preferredShowClass) {
		this.preferredShowClass = preferredShowClass;
		this.includes = includes;
	}

	@Override
	public void before(InterceptorContext ctx) {
		String sqlId = ctx.getSqlId();
		if (this.isDebugEanble(sqlId)) {
			ctx.put("debug.time", System.currentTimeMillis());
		}
		if (this.isSimple(sqlId)) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator", "\n");
		sb.append("┏━━━━━ Debug [").append(this.getSqlId(formatSql(sqlId))).append("] ━━━").append(lineSeparator)
				.append("┣ SQL：\t " + formatSql(ctx.getSql()) + lineSeparator)
				.append("┣ 参数：\t " + formatParas(ctx.getParas())).append(lineSeparator);
		RuntimeException ex = new RuntimeException();
		StackTraceElement[] traces = ex.getStackTrace();
		int index = lookBusinessCodeInTrace(traces);
		StackTraceElement bussinessCode = traces[index];
		String className = bussinessCode.getClassName();
		String mehodName = bussinessCode.getMethodName();
		int line = bussinessCode.getLineNumber();
		sb.append("┣ 位置：\t " + className + "." + mehodName + "(" + bussinessCode.getFileName() + ":" + line + ")"
				+ lineSeparator);

		ctx.put("logs", sb);
	}

	protected int lookBusinessCodeInTrace(StackTraceElement[] traces) {

		String className = getTraceClassName();
		for (int i = traces.length - 1; i >= 0; i--) {
			String name = traces[i].getClassName();
			if (className != null && className.equals(name)) {
				return i;

			} else if (name.equals(mapperName)) {
				// 越过sun jdk 代理
			    int skipLine = JavaType.isJdk8()?3:2;
				return i + skipLine;
			} else if(name.equals(lambdaQueryName)) {
			    return i +1;
			}else if(name.equals(queryClassName)) {
			    return i+1;
			}
			else if (name.equals(sqlManager)) {
			    
				return i + 1;
			}
		}
		// 不可能到这里
		throw new IllegalStateException();

	}
	
	

	/**
	 * 如果自己封装了beetlsql 有自己的util，并不想打印util类，而是业务类，可以在这里写util类
	 * 
	 * @return
	 */
	protected String getTraceClassName() {
		return preferredShowClass;
	}

	@Override
	public void after(InterceptorContext ctx) {
		String sqlId = ctx.getSqlId();
		if (this.isSimple(sqlId)) {
			this.simpleOut(ctx);
			return;
		}
		long time = System.currentTimeMillis();
		long start = (Long) ctx.get("debug.time");
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb = (StringBuilder) ctx.get("logs");
		sb.append("┣ 时间：\t " + (time - start) + "ms").append(lineSeparator);
		if (ctx.isUpdate()) {
			sb.append("┣ 更新：\t [");
			if (ctx.getResult().getClass().isArray()) {
				int[] ret = (int[]) ctx.getResult();
				for (int i = 0; i < ret.length; i++) {
					if (i > 0)
						sb.append(",");
					sb.append(ret[i]);
				}
			} else {
				sb.append(ctx.getResult());
			}
			sb.append("]").append(lineSeparator);
		} else {
			if (ctx.getResult() instanceof Collection) {
				sb.append("┣ 结果：\t [").append(((Collection) ctx.getResult()).size()).append("]").append(lineSeparator);
			} else {
				sb.append("┣ 结果：\t [").append(ctx.getResult()).append("]").append(lineSeparator);
			}

		}
		sb.append("┗━━━━━ Debug [").append(this.getSqlId(formatSql(ctx.getSqlId()))).append("] ━━━")
				.append(lineSeparator);
		println(sb.toString());

	}

	protected boolean isDebugEanble(String sqlId) {
		if (this.includes == null)
			return true;
		for (String id : includes) {
			if (sqlId.startsWith(id)) {
				return true;
			}
		}
		return false;
	}

	protected List<String> formatParas(List<SQLParameter> list) {
		List<String> data = new ArrayList<String>(list.size());
		for (SQLParameter para : list) {
			Object obj = para.value;
			if (obj == null) {
				data.add(null);
			} else if (obj instanceof String) {
				String str = (String) obj;
				if (str.length() > 60) {
					data.add(str.substring(0, 60) + "...(" + str.length() + ")");
				} else {
					data.add(str);
				}
			} else if (obj instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				data.add(sdf.format((Date) obj));
			} else if (obj instanceof Enum) {
				Object value = EnumKit.getValueByEnum(obj);
				data.add(String.valueOf(value));
			} else {
				data.add(obj.toString());
			}
		}
		return data;
	}

	protected void println(String str) {
		System.out.println(str);
	}

	protected String getSqlId(String sqlId) {
		if (sqlId.length() > 50) {
			sqlId = sqlId.substring(0, 50);
			sqlId = sqlId + "...";
		}
		return sqlId;
	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {
		String sqlId = ctx.getSqlId();
		if (this.isSimple(sqlId)) {
			this.simpleOutException(ctx, ex);
			return;
		}
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb = (StringBuilder) ctx.get("logs");
		sb.append("┗━━━━━ Debug [ ERROR:").append(ex != null ? ex.getMessage().replace(lineSeparator, "") : "")
				.append("] ━━━").append(lineSeparator);
		println(sb.toString());

	}

	protected String formatSql(String sql) {
		return sql.replaceAll("--.*", "").replaceAll("\\s+", " ");
	}

	protected boolean isSimple(String sqlId) {
		return false;
	}

	protected void simpleOut(InterceptorContext ctx) {
		String sqlId = ctx.getSqlId();
		StringBuilder sb = new StringBuilder();
		sb.append("--BeetlSql:").append(sqlId).append(", paras:").append(formatParas(ctx.getParas()));
		this.println(sb.toString());
		return;
	}

	protected void simpleOutException(InterceptorContext ctx, Exception ex) {
		String sqlId = ctx.getSqlId();
		StringBuilder sb = new StringBuilder();
		sb.append("--BeetlSql Error:");
		sb.append(ex != null ? ex.getMessage() : "");
		sb.append(" 位于 ").append(sqlId).append(", paras:").append(formatParas(ctx.getParas()));

		this.println(sb.toString());
		return;
	}

}
