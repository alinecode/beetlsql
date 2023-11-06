package org.beetl.sql.ext;

import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.clazz.EnumKit;
import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.JavaType;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SQLManager;
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
 * Debug重新美化版本,把sql执行语句，参数，时间，以及此sql在代码中执行的位置(可选)打印到控制台
 * 如果对性能有要求，不建议使用此Interceptor
 *
 * @author xiandafu
 * @author darren
 * @author 最后的夏天
 * @version 2016年8月25日
 */
@Slf4j
public class Slf4JLogInterceptor implements Interceptor {

    protected static final String LINE_SEPARATOR = System.lineSeparator();

    protected static String mapperName = "org.beetl.sql.mapper.MapperJavaProxy";

    protected static String sqlManager = SQLManager.class.getName();

    protected static String queryClassName = Query.class.getName();

    protected static String lambdaQueryName = LambdaQuery.class.getName();

    protected static String defaultQueryMethod = QueryExecuteI.class.getName();

    /**
     * 显示sql所在行号, 开销较大
     * // TODO 预留，后面需要做配置
     */
    protected boolean showLineNum;

    /**
     * 自定义输出行号时优先输出的类，而不是SQLManager或者是BaseMapper
     * // TODO 预留，后面需要做配置
     */
    protected String preferredShowClass;

    public Slf4JLogInterceptor() {

    }

    public Slf4JLogInterceptor(boolean showLineNum, String preferredShowClass) {
        this.showLineNum = showLineNum;
        this.preferredShowClass = preferredShowClass;
    }



    /**
     * SQL执行前调用
     *
     * @param ctx {@code InterceptorContext}
     */
    @Override
    public void before(InterceptorContext ctx) {
        if (!log.isDebugEnabled()) {
            return;
        }

        ExecuteContext executeContext = ctx.getExecuteContext();
        String sqlId = executeContext.sqlId.toString();
        String jdbcSql = ctx.getExecuteContext().sqlResult.jdbcSql;
        ctx.put("debug.time", System.currentTimeMillis());

        StringBuilder sb = new StringBuilder(formatSqlId(sqlId)).append(LINE_SEPARATOR);
        sb.append("┌ SQL:\t").append(formatSql(jdbcSql)).append(LINE_SEPARATOR)
                .append("├ 参数:\t").append(formatParas(ctx.getExecuteContext().sqlResult.jdbcPara))
                .append(LINE_SEPARATOR);
        if (showLineNum) {
            RuntimeException ex = new RuntimeException();
            StackTraceElement[] traces = ex.getStackTrace();
            int index = lookBusinessCodeInTrace(traces);
            StackTraceElement businessCode = traces[index];
            String className = businessCode.getClassName();
            String methodName = businessCode.getMethodName();
            int line = businessCode.getLineNumber();
            sb.append("├ 位置:\t").append(className).append('.').append(methodName)
                    .append('(').append(businessCode.getFileName()).append(':')
                    .append(line).append(')').append(LINE_SEPARATOR);
        }
        ctx.put("logs", sb);
    }

    /**
     * SQL正常执行后调用
     *
     * @param ctx {@code InterceptorContext}
     */
    @Override
    public void after(InterceptorContext ctx) {
        if (!log.isDebugEnabled()) {
            return;
        }

        long start = (long) ctx.get("debug.time");
        StringBuilder sb = (StringBuilder) ctx.get("logs");
        sb.append("├ 时间:\t").append(System.currentTimeMillis() - start).append("ms").append(LINE_SEPARATOR);
        SQLType sqlType = ctx.getExecuteContext().sqlSource.sqlType;
        Object result = ctx.getExecuteContext().executeResult;
        if (sqlType.isUpdate()) {
            sb.append("└ 更新:\t[");
            if (result.getClass().isArray()) {
                int[] ret = (int[]) result;
                for (int i = 0; i < ret.length; i++) {
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(ret[i]);
                }
            } else {
                sb.append(result);
            }
            sb.append(']').append(LINE_SEPARATOR);
        } else {
            if (result instanceof Collection) {
                sb.append("└ 结果:\t[").append(((Collection) result).size()).append(']');
            } else {
                sb.append("└ 结果:\t[").append(result).append(']');
            }
        }
        print(sb.toString());
    }

    /**
     * SQL执行异常后调用
     *
     * @param ctx {@code InterceptorContext}
     * @param ex  SQL执行异常信息
     */
    @Override
    public void exception(InterceptorContext ctx, Exception ex) {
        if (log.isDebugEnabled()) {
            StringBuilder sb = (StringBuilder) ctx.get("logs");
            sb.append("└ 异常:\t").append(ex.getMessage());
            error(sb.toString());
        }
    }

    protected String formatSqlId(String id) {
        String sql = formatSql(id);
        return sql.length() > 50 ? sql.substring(0, 50) + "..." : sql;
    }

    protected int lookBusinessCodeInTrace(StackTraceElement[] traces) {
        for (int i = traces.length - 1; i >= 0; i--) {
            String name = traces[i].getClassName();
            if (preferredShowClass != null && preferredShowClass.equals(name)) {
                return i;
            } else if (name.equals(mapperName)) {
                // 越过sun jdk 代理
                int skipLine = JavaType.isJdk8() ? 3 : 2;
                return i + skipLine;
            } else if (name.equals(lambdaQueryName)) {
                return i + 1;
            } else if (name.equals(queryClassName)) {
                return i + 1;
            } else if (name.equals(defaultQueryMethod)) {
                return i + 1;
            } else if (name.equals(sqlManager)) {
                return i + 1;
            }
        }
        // 不可能到这里
        throw new IllegalStateException();
    }

    protected List<String> formatParas(List<SQLParameter> list) {
        List<String> data = new ArrayList<>(list.size());
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

    protected void print(String str) {
        log.debug(str);
    }

    protected void error(String str) {
        log.error(str);
    }

}
