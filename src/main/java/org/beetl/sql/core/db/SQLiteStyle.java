package org.beetl.sql.core.db;

import java.util.Map;

/**
 * SQLite 数据库
 * Created by mikey.zhaopeng on 2015/11/18.
 * http://zhaopeng.me
 */
public class SQLiteStyle extends AbstractDBStyle {

    public SQLiteStyle() {
        this.keyWordHandler = new KeyWordHandler() {
            @Override
            public String getTable(String tableName) {
                return "`" + tableName + "`";

            }

            @Override
            public String getCol(String colName) {
                return "`" + colName + "`";
            }

        };
    }


    @Override
    public String getPageSQL(String sql) {
        return sql + this.getOrderBy() + " \nlimit " + HOLDER_START + PAGE_SIZE + HOLDER_END + " offset " + HOLDER_START + OFFSET + HOLDER_END;
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {
        offset = PageParamKit.sqlLiteOffset(this.offsetStartZero, offset);
        int capacity = sql.length() + 28;
        StringBuilder builder = new StringBuilder(capacity);
        builder.append(" limit ").append(pageSize).append(" offset ").append(offset);
        return builder.toString();
    }

    @Override
    public void initPagePara(Map<String, Object> param, long start, long size) {
        param.put(DBStyle.OFFSET, start - (this.offsetStartZero ? 0 : 1));
        param.put(DBStyle.PAGE_SIZE, size);
    }


    @Override
    public String getName() {
        return "sqlite";
    }


    @Override
    public int getDBType() {
        return DB_SQLLITE;
    }


}
