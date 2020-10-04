package org.beetl.sql.mapper.provider;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.annotation.SqlProvider;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 实现{@code SqlProvider},mapper的sql语句由SqlProvider类来提供，
 * @author xiandafu
 */
public class SqlPMI extends BaseSqlPMI {
    SqlProvider sqlProvider = null;
    boolean isSelect = false;
    boolean isSingle = false;
    Class targetType = null;
    public SqlPMI(SqlProvider sqlProvider, Class targetType, boolean isSelect, boolean single){
        this.sqlProvider = sqlProvider;
        this.isSelect = isSelect;
        this.isSingle = single;
        this.targetType = targetType;
    }
    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        SQLReady ready = getSQLReadyByProvider(sqlProvider,m,args);

        if(isSelect){
            List list = sm.execute(ready,targetType);
            if(isSingle){
                return list.isEmpty()?null:list.get(0);
            }else{
                return list;
            }
        }else{
            return sm.executeUpdate(ready);
        }
    }


}
