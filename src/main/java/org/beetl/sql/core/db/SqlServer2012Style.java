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
    public String getPageSQL(String sql) {
        StringBuilder builder = new StringBuilder(sql).append(this.getOrderBy());
      //sqlserver 2012 以上的 offset 分页必须要跟在order by后面，因此如果语句本身没有order by则为其添加一个按默认时间戳的order by
        if(!builder.toString().matches("(?i).* order by[^)]+$")) {
        	builder.append(" order by current_timestamp");
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
        //sqlserver 2012 以上的 offset 分页必须要跟在order by后面，因此如果语句本身没有order by则为其添加一个按默认时间戳的order by
        if(!sql.matches("(?i).* order by[^)]+$")) {
        	builder.append(" order by current_timestamp");
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