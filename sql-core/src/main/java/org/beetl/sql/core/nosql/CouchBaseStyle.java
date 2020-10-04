package org.beetl.sql.core.nosql;

import org.beetl.sql.clazz.kit.SpecialKeyWordHandlder;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.ReadTypeParameter;
import org.beetl.sql.core.mapping.type.WriteTypeParameter;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

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
        this.keyWordHandler = new SpecialKeyWordHandlder();
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
