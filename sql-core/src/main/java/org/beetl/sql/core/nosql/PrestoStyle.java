package org.beetl.sql.core.nosql;

import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.QuerySQLExecutor;
import org.beetl.sql.core.SQLExecutor;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.range.OffsetLimitRange;
import org.beetl.sql.core.range.RangeSql;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 数据库差异：presto数据库,注意presto 不支持jdbc的Preparedtatment 以及不支持offset
 *
 * @author xiandafu
 */
public class PrestoStyle extends AbstractDBStyle {

    RangeSql rangeSql = null;

    public PrestoStyle() {
        super();
        rangeSql = new CassandraSqlStyle.CassandraRangeSql(this);
    }


    @Override
    public boolean  isNoSql(){
        return true;
    }

    /**
     * 对应的数据库是否支持jdbc metadata
     * @return
     */
    @Override
    public  boolean metadataSupport(){
        return true ;
    }

    /**
     *
     * @return
     */
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


    @Override
    public int getIdType(Class c,String idProperty) {
        List<Annotation> ans = BeanKit.getAllAnnotation(c, idProperty);
        int idType = DBType.ID_ASSIGN; //默认是自增长
        return idType;

    }

    @Override
    public String getName() {
        return "presto";
    }

    @Override
    public int getDBType() {
        return DBType.PRESTO_ES;
    }

    @Override
    public RangeSql getRangeSql() {
        return this.rangeSql;
    }



}
