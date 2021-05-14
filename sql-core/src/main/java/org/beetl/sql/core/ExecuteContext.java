package org.beetl.sql.core;

import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.ResultSetMapper;
import org.beetl.sql.core.mapping.RowMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * beetlsql的上下文，任何插件都可以获得sql的执行情况，sql的执行结果
 * @author xiandafu
 * @since 3.0
 */

public class ExecuteContext {

    /**
     * sqlId
     */
    public SqlId sqlId ;
    /**
     * select 语句需要映射的对象，有可能没有，比如update语句
     */
    public Class target;

    /**
     * 原始参数
     */
    public Object inputParas;

    /**
     * sql模板
     */
    public SQLSource sqlSource;


    /**
     * ViewType类型，如果viewType不为null
     */
    public Class<?> viewClass = null;
    /**
     * 行映射类，与resultMapper只能二选一存在
     */
    public RowMapper<?> rowMapper = null;
    /**
     * Bean映射类
     */
    public ResultSetMapper<?> resultMapper = null;

    /**
     * 用来负责将ResultSet映射到对象上，如果此不为null，则使用此类负责映射，
     * 否则，参考RowMapper或者ResultSetMapper；<br/>
	 * 如果为null，则使用SQLManager的默认的BeanProcessor
     */
    public BeanProcessor beanProcessor = null;


    public SQLManager sqlManager;

    /**
     * sql模板渲染后的sql语句和参数
     */
    public SQLResult sqlResult = new SQLResult();

    /**
     * Executor执行结果,非convert，fetch扩展操作结果
     */
    public Object executeResult;

    /**
     * 在执行过程中的产生控制
     */
    public Map<String,Object> contextParas;


    public static String NAME = "_executeContext";

    public static String ROOT_PARAM = "_root";

    public boolean isUpdate = false;

    public static ExecuteContext instance(SQLManager sqlManager){
        ExecuteContext executeContext =  new ExecuteContext();
        executeContext.sqlManager = sqlManager;
        QueryConfig queryConfig = sqlManager.queryConfigLocal.get();
        if(queryConfig!=null){
            executeContext.viewClass = queryConfig.getViewClass();
            executeContext.rowMapper = queryConfig.getRowMapper();
            executeContext.resultMapper = queryConfig.getResultSetMapper();
            queryConfig.clear();
        }

        return executeContext;
    }


    public ExecuteContext initSQLSource(SQLSource sqlSource){
        this.sqlSource = sqlSource;
        this.sqlId = sqlSource.getId();
        return this;
    }

    public Object getContextPara(String key){
        if(contextParas==null){
            return null;
        }
        return contextParas.get(key);
    }

    /**
     * 设置一些额外的变量，供执行过程后面使用
     * @param key
     * @param obj
     */
    public void setContextPara(String key,Object obj){
        if(contextParas==null){
            contextParas = new HashMap<>(6);
        }
        contextParas.put(key,obj);
    }


    public void fill(ThreadLocal<QueryConfig> queryConfigLocal){
        QueryConfig queryConfig = queryConfigLocal.get();
        if(queryConfig!=null){
            viewClass = queryConfig.getViewClass();
            resultMapper = queryConfig.getResultSetMapper();
            rowMapper = queryConfig.getRowMapper();
            queryConfigLocal.set(null);
        }
    }



}
