package org.beetl.sql.core.db;

import java.util.Map;

/**
 * SQL Server 2012以上版本请使用此DBStyle，对翻页做了优化
 * @author darren
 *
 */
public class SqlServer2012Style extends SqlServerStyle {

    public SqlServer2012Style() {
        super();
    }

    @Override
    protected String getOrderBy() {
        //重写getOrderBy，如果设置了分页的order by条件 则按 order by 否则添加一个 current_timestamp 来排序
        return lineSeparator + HOLDER_START + "text(' order by ' + _orderBy!'current_timestamp')" + HOLDER_END + " ";
    }

    @Override
    public String getPageSQL(String sql) {
        StringBuilder builder = new StringBuilder(sql);
        //sqlserver 2012 以上的 offset 分页必须要跟在order by后面
        //因此如果语句本身没有order by则为其添加一个按默认时间戳的order by
        //先判断 是否有包含order by条件的 pageIgnoreTag函数 或者单纯的order by xxx结尾
        //有则忽略不再拼接条件，没有则拼接一个定制的getOrderBy函数
        if(!sql.matches("(?is).*pageIgnoreTag\\s*\\(\\s*\\)[^}]*?order\\s+by.*|.*\\s+order\\s+by[^)]+$")) {
        	builder.append(this.getOrderBy());
        }
        return builder.append(" offset ")
        .append(HOLDER_START).append(OFFSET).append(HOLDER_END)
        .append(" rows fetch next ")
        .append(HOLDER_START).append(PAGE_SIZE).append(HOLDER_END)
        .append(" rows only ").toString();
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {
        StringBuilder builder = new StringBuilder(sql);
        //sqlserver 2012 以上的 offset 分页必须要跟在order by后面
        //因此如果语句本身没有order by则为其添加一个按默认时间戳的order by
        if(!sql.matches("(?is).*\\s+order\\s+by[^)]+$")) {
        	builder.append(" order by current_timestamp ");
        }
        return builder.append(" offset ")
                .append(PageParamKit.sqlServer2012Offset(this.offsetStartZero, offset))
                .append(" rows fetch next ")
                .append(pageSize)
                .append(" rows only ").toString();
    }
    
    @Override
    public void initPagePara(Map<String, Object> param, long start, long size) {
        param.put(DBStyle.OFFSET, start - (this.offsetStartZero ? 0 : 1));
        param.put(DBStyle.PAGE_SIZE, size);
    }

}