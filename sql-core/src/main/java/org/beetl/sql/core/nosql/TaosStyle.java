package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SQLTableSource;
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


    /**
     * taos 不支持 where 1=1，因此内置语句需要把出现的1=1 替换掉
     * @param cls
     * @param viewType
     * @return
     */
    @Override
    public SQLSource genSelectByTemplate(Class<?> cls, Class viewType) {
        SQLTableSource sqlTableSource = (SQLTableSource)super.genSelectByTemplate(cls,viewType);
        String sql =  sqlTableSource.getTemplate();
        String after = replaceByWhereTag(sql);
        sqlTableSource.setTemplate(after);
        return sqlTableSource;

    }

    @Override
    public SQLSource genSelectCountByTemplate(Class<?> cls) {
        SQLTableSource sqlTableSource = (SQLTableSource)super.genSelectCountByTemplate(cls);
        String sql =  sqlTableSource.getTemplate();
        String after = replaceByWhereTag(sql);
        sqlTableSource.setTemplate(after);
        return sqlTableSource;
    }

    //一个临时方案，替换1=1部分，因为taos不支持1=1表达式
    protected  String replaceByWhereTag(String sql){
        String trimStart = "\n-- @ where(){";
        sql=sql.replace("where 1=1",trimStart);
        sql =sql +"\n -- @ }";
        return sql;
    }






}
