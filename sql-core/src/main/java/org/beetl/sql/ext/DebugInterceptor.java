package org.beetl.sql.ext;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.EnumKit;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.*;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Debug重新美化版本,把sql执行语句，参数，和时间，以及此sql在代码中执行的位置打印到控制台
 * 如果对性能有要求，不建议使用此Interceptor
 *
 *
 * @author darren xiandafu
 * @version 2016年8月25日
 *
 */
public class DebugInterceptor implements Interceptor {

	protected static String mapperName = "org.beetl.sql.mapper.MapperJavaProxy";
	protected static String sqlManager = SQLManager.class.getName();
	protected static String queryClassName = Query.class.getName();
	protected static String lambdaQueryName = LambdaQuery.class.getName();
	protected static String defaultQueryMethod = QueryExecuteI.class.getName();
	// debug 输入优先输出的类，而不是SQLManager或者是BaseMapper
	String preferredShowClass;

	public DebugInterceptor() {
	}



	public DebugInterceptor(String preferredShowClass) {
		this.preferredShowClass = preferredShowClass;

	}

	//Override
	@Override
	public void before(InterceptorContext ctx) {
		ExecuteContext executeContext = ctx.getExecuteContext();
		SqlId sqlId = executeContext.sqlId;
		String jdbcSql = ctx.getExecuteContext().sqlResult.jdbcSql;
		if (this.isDebugEnable(sqlId)) {
			ctx.put("debug.time", System.currentTimeMillis());
		}
		if (this.isSimple(sqlId)) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator", "\n");
		sb.append("┏━━━━━ Debug [").append(formatSqlId(executeContext)).append("] ━━━").append(lineSeparator)
				.append("┣ SQL：\t " + formatSql(jdbcSql) + lineSeparator)
				.append("┣ 参数：\t " + formatParas(ctx.getExecuteContext().sqlResult.jdbcPara)).append(lineSeparator);
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

	protected String formatSqlId(ExecuteContext executeContext){
		SqlId id = executeContext.sqlId;
		String str = id.toString();
		String sql = formatSql(str);
		if(sql.length()>50){
			return sql.substring(0,50)+"...";
		}else{
			return sql;
		}

	}
	protected int lookBusinessCodeInTrace(StackTraceElement[] traces) {

		String className = getTraceClassName();
		int sqlMangerIndex = -1;
		for (int i = traces.length - 1; i >= 0; i--) {
			String name = traces[i].getClassName();
			if (className != null && className.equals(name)) {
				return i;
			}else if (name.equals(sqlManager)) {
				sqlMangerIndex = i+1;
			}
		}

		for (int i = sqlMangerIndex; i < traces.length - 1; i++) {
			String name = traces[i].getClassName();
			if (name.equals(mapperName)) {
				for(i=i+1;i<traces.length ;i++){
					name = traces[i].getClassName();
					if(name.startsWith("com.sun.proxy.$Proxy")){
						return i+1;
					}
				}
				return i + 1;
			} else if (name.startsWith("org.beetl.sql.core.query")) {
				for(i=i+1;i<traces.length;i++){
					name = traces[i].getClassName();
					if(!name.startsWith("org.beetl.sql.core.query")){
						return i;
					}
				}
			}
		}
		return sqlMangerIndex;

	}

	/**
	 * 如果自己封装了beetlsql 有自己的util，并不想打印util类，而是业务类，可以在这里写util类
	 *
	 * @return
	 */
	protected String getTraceClassName() {
		return preferredShowClass;
	}

	//Override
	@Override
	public void after(InterceptorContext ctx) {
		ExecuteContext executeContext = ctx.getExecuteContext();
		SqlId sqlId = executeContext.sqlId;
		if (this.isSimple(sqlId)) {
			this.simpleOut(ctx);
			return;
		}
		long time = System.currentTimeMillis();
		long start = (Long) ctx.get("debug.time");
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb = (StringBuilder) ctx.get("logs");
		sb.append("┣ 时间：\t " + (time - start) + "ms").append(lineSeparator);
		SQLType sqlType = ctx.getExecuteContext().sqlSource.sqlType;
		Object result = ctx.getExecuteContext().executeResult;
		if (sqlType.isUpdate()) {
			sb.append("┣ 更新：\t [");

			if (result.getClass().isArray()) {
				int[] ret = (int[]) result;
				for (int i = 0; i < ret.length; i++) {
					if (i > 0) {
						sb.append(",");
					}
					sb.append(ret[i]);
				}
			} else {
				sb.append(result);
			}
			sb.append("]").append(lineSeparator);
		} else {
			if (result instanceof Collection) {
				sb.append("┣ 结果：\t [").append(((Collection) result).size()).append("]").append(lineSeparator);
			} else {
				sb.append("┣ 结果：\t [").append(result).append("]").append(lineSeparator);
			}
		}
		sb.append("┗━━━━━ Debug [").append(formatSqlId(executeContext)).append("] ━━━")
				.append(lineSeparator);
		println(sb.toString());
	}

	protected boolean isDebugEnable(SqlId sqlId) {
		return true;
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss.SSS)");
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

	protected void error(String str) {
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
		SqlId sqlId = ctx.getExecuteContext().sqlId;
		if (this.isSimple(sqlId)) {
			this.simpleOutException(ctx, ex);
			return;
		}
		String lineSeparator = System.getProperty("line.separator", "\n");
		StringBuilder sb = (StringBuilder) ctx.get("logs");
		sb.append("┗━━━━━ Debug [ ERROR:").append(ex != null ? ex.getMessage().replace(lineSeparator, "") : "")
				.append("] ━━━").append(lineSeparator);
		error(sb.toString());
	}

	public static String formatSql(String sql) {
		return sql.replaceAll("--.*", "").replaceAll("\\n","").replaceAll("\\s+", " ");
	}

	protected boolean isSimple(SqlId sqlId) {
		return false;
	}

	protected void simpleOut(InterceptorContext ctx) {
		SqlId sqlId = ctx.getExecuteContext().sqlId;
		String sql = ctx.getExecuteContext().sqlResult.jdbcSql;
		StringBuilder sb = new StringBuilder();
		sb.append("BeetlSQL Debug:[").append(formatSql(sql)).append("], paras:").append(formatParas(ctx.getExecuteContext().sqlResult.jdbcPara));
		this.println(sb.toString());
		return;
	}

	protected void simpleOutException(InterceptorContext ctx, Exception ex) {
		StringBuilder sb = new StringBuilder();
		sb.append("BeetlSQL Debug Error  ");
		sb.append(ex != null ? ex.getMessage() : "");
		this.error(sb.toString());
		return;
	}

}
