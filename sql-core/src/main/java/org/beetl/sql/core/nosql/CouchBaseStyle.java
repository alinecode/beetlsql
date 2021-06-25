package org.beetl.sql.core.nosql;

import org.beetl.sql.clazz.kit.SpecialKeyWordHandler;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.RangeSql;

/**
 *  clickhouse
 *
 * @author xiandafu
 * @see "https://clickhouse.tech/"
 */
public class CouchBaseStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;
    public CouchBaseStyle() {
        super();
        rangeSql = new LimitWithOffsetRange(this);
        this.keyWordHandler = new SpecialKeyWordHandler();
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
        return "couchbase";
    }

    @Override
    public int getDBType() {
        return DBType.DB_COUCHBASE;
    }

    @Override
    public RangeSql getRangeSql() {
        return rangeSql;
    }


}
