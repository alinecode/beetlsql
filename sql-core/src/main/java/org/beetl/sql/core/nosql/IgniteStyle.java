package org.beetl.sql.core.nosql;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.RangeSql;

/**
 * Ignite内存数据库
 * @author xiandafu
 * @see "https://apacheignite.readme.io/docs/getting-started"
 * @see "https://apacheignite-sql.readme.io/docs/jdbc-driver"
 */
public class IgniteStyle    extends AbstractDBStyle {

    RangeSql rangeSql = null;
    public IgniteStyle() {
        super();
        rangeSql = new LimitWithOffsetRange(this);
    }

    @Override
    public int getIdType(Class c,String idProperty) {
        //只支持
        return DBType.ID_ASSIGN;
    }

    @Override
    public boolean  isNoSql(){
        return true;
    }
    @Override
    public String getName() {
        return "ignite";
    }

    @Override
    public int getDBType() {
        return DBType.DB_IGNITE;
    }

    @Override
    public RangeSql getRangeSql() {
        return rangeSql;
    }


}
