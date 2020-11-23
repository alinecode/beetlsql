package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.QuerySQLExecutor;
import org.beetl.sql.core.SQLExecutor;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.range.LimitWithOffsetRange;
import org.beetl.sql.core.range.RangeSql;

/**
 * apache drill 查询引擎
 *
 * @author xiandafu
 * @see "https://github.com/smizy/docker-apache-drill"
 */
public class DrillStyle extends AbstractDBStyle {

    LimitWithOffsetRange range = null;
    public DrillStyle() {
        super();
        range = new LimitWithOffsetRange(this);
    }


    @Override
    public int getIdType(Class c,String idProperty) {
        //只支持assign
        return DBType.ID_ASSIGN;
    }

    @Override
    public String getName() {
        return "drill";
    }

    @Override
    public int getDBType() {
        return DBType.DB_DRILL;
    }

    @Override
    public RangeSql getRangeSql() {
        return range;
    }

    /**
     * drill 没有shcema定义，因此返回的是NoSchemaMetaDataManager
     * @param cs
     * @return
     */
    @Override
    public MetadataManager initMetadataManager(ConnectionSource cs){
        metadataManager = new SchemaLessMetaDataManager(cs,this);
        return metadataManager;
    }

    /**
     *  drill 没有shcema定义，因此返回的是NoSchemaMetaDataManager
     * @param cs
     * @param defaultSchema
     * @param defaultCatalog
     * @return
     */
    @Override
    public SchemaLessMetaDataManager initMetadataManager(ConnectionSource cs, String defaultSchema, String defaultCatalog){
        metadataManager = new SchemaLessMetaDataManager(cs,this);
        return (SchemaLessMetaDataManager)metadataManager;
    }

    @Override
    public boolean metadataSupport(){
        return false ;
    }

    @Override
    public  boolean preparedStatementSupport(){
        return false;
    }



    @Override
    public String wrapStatementValue(Object value){
       return super.wrapStatementValue(value);
    }
    @Override
    public SQLExecutor buildExecutor(ExecuteContext executeContext){
        return new QuerySQLExecutor(executeContext);
    }

}
