package org.beetl.sql.core;

import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.clazz.*;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.engine.template.SQLTemplate;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.engine.template.TemplateContext;
import org.beetl.sql.core.mapping.*;
import org.beetl.sql.core.meta.MetadataManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * 用于查询引擎，只支持查询
 *
 * @author xiandafu
 * @see BaseStatementOnlySQLExecutor
 */
public class QuerySQLExecutor extends BaseSQLExecutor {

    public QuerySQLExecutor(ExecuteContext executeContext) {
        super(executeContext);
    }

    public int insert(Class target, Object paras){
        throw new UnsupportedOperationException("Query only support");
    }


    public Object[] insert(Class target,Object paras, String[] cols){
        throw new UnsupportedOperationException("Query only support");
    }


    public int update(Class target,Object obj){
        throw new UnsupportedOperationException("Query only support");
    }

    public int[] updateBatch(List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }

    public int[] updateBatch(Class<?> target ,List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }

    public int[] insertBatch(Class<?> target,List<?> list){
        throw new UnsupportedOperationException("Query only support");
    }


    public int deleteById(Class<?> target, Object objId){
        throw new UnsupportedOperationException("Query only support");
    }

    public int sqlReadyExecuteUpdate(SQLReady p){
        throw new UnsupportedOperationException("Query only support");
    }

    public int[] sqlReadyBatchExecuteUpdate(SQLBatchReady batch){
        throw new UnsupportedOperationException("Query only support");
    }
}
