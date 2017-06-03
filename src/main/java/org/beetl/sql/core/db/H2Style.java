package org.beetl.sql.core.db;

import java.util.Map;

/**
 * 数据库差异：h2.
 *
 * @author zhoupan
 */
public class H2Style extends AbstractDBStyle {

    public H2Style() {
        super();
    }

    public String getPageSQL(String sql) {
        return sql + this.getOrderBy() + " \nlimit " + HOLDER_START + OFFSET + HOLDER_END + " , " + HOLDER_START + PAGE_SIZE + HOLDER_END;
    }

    @Override
    public String getPageSQLStatement(String sql, long offset, long pageSize) {

        int capacity = sql.length() + 23;
        StringBuilder builder = new StringBuilder(capacity);
        builder.append(" limit ").append(offset).append(" , ").append(pageSize);
        return builder.toString();
    }

    public void initPagePara(Map<String, Object> param, long start, long size) {
        param.put(DBStyle.OFFSET, start);
        param.put(DBStyle.PAGE_SIZE, size);
    }

    public String getName() {
        return "h2";
    }

    public String getEscapeForKeyWord() {
        return "\"";
    }

    @Override
    public int getDBType() {
        return DB_H2;
    }


}
