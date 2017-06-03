package org.beetl.sql.core.db;

import java.util.Map;

public class PostgresStyle extends AbstractDBStyle {

    public PostgresStyle() {
    }

    @Override
    public String getPageSQL(String sql) {
        String pageSql = "select _a.* from ( \n"
                + sql + this.getOrderBy()
                + " \n) _a "
                + " limit " + HOLDER_START + this.PAGE_SIZE + HOLDER_END + " offset " + HOLDER_START + this.OFFSET + HOLDER_END;
        return pageSql;
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {

        offset = PageParamKit.postgresOffset(this.offsetStartZero, offset);

        int capacity = sql.length() + 50;

        StringBuilder builder = new StringBuilder(capacity);
        builder.append("select _a.* from ( ").append(sql).append(" ) _a ");
        builder.append("limit ").append(pageSize).append(" offset ").append(offset);
        return builder.toString();
    }

    @Override
    public void initPagePara(Map<String, Object> paras, long start, long size) {
        paras.put(DBStyle.OFFSET, start - (this.offsetStartZero ? 0 : 1));
        paras.put(DBStyle.PAGE_SIZE, size);
    }


    @Override
    public String getName() {
        return "postgres";
    }

    @Override
    public int getDBType() {

        return DB_POSTGRES;
    }


}
