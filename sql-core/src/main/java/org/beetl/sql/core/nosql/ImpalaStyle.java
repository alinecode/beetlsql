package org.beetl.sql.core.nosql;

import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.util.Collection;

/**
 * https://hub.docker.com/r/cloudera/impala-dev/
 * @author xiandafu
 */
public class ImpalaStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;

    public ImpalaStyle() {
        super();
        //同mysql
        rangeSql = new OffsetLimitRange(this);
    }

    @Override
    public int getIdType(Class c, String idProperty) {
        //只支持
        return DBType.ID_ASSIGN;
    }

    @Override
    public boolean isNoSql() {
        return true;
    }

    @Override
    public String getName() {
        return "clickhouse";
    }

    @Override
    public int getDBType() {
        return DBType.DB_CLICKHOUSE;
    }

    @Override
    public RangeSql getRangeSql() {
        return rangeSql;
    }

    protected void checkId(Collection colsId, Collection attrsId, String clsName) {
        // 不检测主键
        return;
    }
}
