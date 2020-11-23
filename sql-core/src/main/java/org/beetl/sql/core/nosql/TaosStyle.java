package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.RangeSql;

import java.util.Collection;

/**
 * 中国的时序数据库
 * @author xiandafu
 */
public class TaosStyle  extends AbstractDBStyle {
    RangeSql rangeSql = null;
    public TaosStyle() {
        super();
        rangeSql = new LimitWithOffsetRange(this);
    }

    @Override
    public int getIdType(Class c,String idProperty) {
        return DBType.ID_ASSIGN;
    }

    @Override
    public boolean  isNoSql(){
        return true;
    }
    @Override
    public String getName() {
        return "taos";
    }

    @Override
    public int getDBType() {
        return DBType.DB_TAOS;
    }

    @Override
    public RangeSql getRangeSql() {
        return rangeSql;
    }

    @Override
    protected void checkId(Collection colsId, Collection attrsId, String clsName) {
        // CLickHouse 不支持主键
        return ;
    }

    /**
     * td-engine目前驱动拿不到metadata，中间会报错
     * @param cs
     * @return
     */
    @Override
    public MetadataManager initMetadataManager(ConnectionSource cs){
        metadataManager = new NoSchemaMetaDataManager();
        return metadataManager;
    }

}
