package org.beetl.sql.core.db;

import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

/**
 * SQLite 数据库
 * Created by mikey.zhaopeng on 2015/11/18.
 * http://zhaopeng.me
 * @author zhaopeng xiandafu
 */
public class SQLiteStyle extends AbstractDBStyle {
    RangeSql rangeSql = null;
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

        rangeSql = new OffsetLimitRange(this);
    }




    @Override
    public String getName() {
        return "sqlite";
    }


    @Override
    public int getDBType() {
        return DBType.DB_SQLLITE;
    }

    @Override
    public RangeSql getRangeSql() {
        return this.rangeSql;
    }


}
