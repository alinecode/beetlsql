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
 * 面向传统数据库的sql 执行引擎
 *
 * @author xiandafu
 * @see BaseStatementOnlySQLExecutor
 */
public class BaseSQLExecutor implements SQLExecutor {

    ExecuteContext executeContext;
    public BaseSQLExecutor(ExecuteContext executeContext) {
        this.executeContext = executeContext;
    }


    @Override
    public int insert(Class clazz, Object paras) {
        KeyHolder holder = paras instanceof Map ? KeyHolder.empty : KeyHolder.getKeyHolderByClass(paras.getClass());
        int ret = insert(paras, holder);
        this.assignKeyHolder(holder, paras);
        return ret;
    }


    @Override
    public Object[] insert(Class target, Object paras, String[] cols) {

        Connection conn = null;
        ResultUpdateHolder ruh = null;
        InterceptorContext ctx = null;
        try {
            this.addParaIfAssignId(paras);
            Map map = this.beforeExecute(target,paras,true);
            SQLResult result = this.run(map);
            String sql = result.jdbcSql;
            List<SQLParameter> jdbcPara = result.jdbcPara;
            ctx = this.callInterceptorAsBefore(map);
            sql = executeContext.sqlResult.jdbcSql;
            jdbcPara = executeContext.sqlResult.jdbcPara;
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            ;
            ruh = dbUpdateWithHolder(conn, sql, jdbcPara, cols);
            Object[] values = null;
            if (cols != null) {
                ResultSet rs = ruh.statement.getGeneratedKeys();
                values = new Object[cols.length];
                int i = 0;
                while (rs.next()) {
                    values[i] = rs.getObject(i + 1);
                    i++;
                }
                rs.close();
            }
            this.callInterceptorAsAfter(ctx, ruh.resultSet);
            return values;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ruh);
        }
    }

    @Override
    public <T> T singleSelect(Class<T> target,Object paras) {
        Map<String, Object> map = this.beforeExecute(target, paras,false);
        return this.selectSingle(map, target);
    }





    @Override
    public <T> T selectUnique(Class<T> target,Object paras) {

        Map map = this.beforeExecute(target, paras,false);
        List<T> result = select(target, map);
        int size = result.size();
        if (size == 1) {
            return result.get(0);
        } else if (size == 0) {
            throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集:参数是" + map);
        } else {
            throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，找到多条记录:参数是" + map);
        }

    }

    @Override
    public <T> List<T> select(Class<T> clazz, Object paras) {
        Map map = this.beforeExecute(clazz, paras,false);
        return this.select(clazz, map);
    }

    protected <T> T selectSingle(Map<String, Object> map, Class<T> target) {
        // 是否翻页来限定，一般调用这个方法，都可能是因为只有一条？
        List<T> result = select(target, map);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    protected <T> List<T> select(Class<T> clazz, Map<String, Object> paras) {
        //运行sql模板，获取实际的sql语句
        SQLResult result = run(paras);

        String sql = result.jdbcSql;
        List<SQLParameter> jdbcPara = result.jdbcPara;
        ResultSetHolder rsh = null;
        List<T> resultList = null;
        InterceptorContext ctx = this.callInterceptorAsBefore(paras);
        if (executeContext.executeResult != null) {
            this.callInterceptorAsAfter(ctx, executeContext.executeResult);
            return (List<T>) executeContext.executeResult;
        }
        //再次获取参数，因为有可能被改变
        sql = executeContext.sqlResult.jdbcSql;
        jdbcPara = executeContext.sqlResult.jdbcPara;
        Connection conn = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
            rsh = this.dbQuery(conn, sql, jdbcPara);


            if (executeContext.beanProcessor != null) {
                resultList = executeContext.beanProcessor.toBeanList(executeContext, rsh.resultSet, clazz);

            } else {
                ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
                //单行映射
                RowMapper rowMapper = executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
                //结果集映
                ResultSetMapper resultSetMapper = executeContext.resultMapper != null ? executeContext.resultMapper : classAnnotation.getResultSetMapper();
                //映射方式三选一
                if (resultSetMapper != null) {
                    resultList = resultSetMapper.mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
                } else if (rowMapper != null) {
                    BeanProcessor beanProcessor = this.getBeanProcessor();
                    resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor).handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
                } else {
                    //默认方式
                    resultList = mappingSelect(clazz,rsh.resultSet);
                }
            }


            this.executeContext.executeResult = resultList;
            this.callInterceptorAsAfter(ctx, resultList);
            resultList = (List)afterBean(resultList);
            return resultList;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(false, conn, rsh);
        }

    }

    @Override
    public <T> List<T> mappingSelect(Class<T> target,ResultSet rs) throws SQLException {
        BeanProcessor beanProcessor = this.getBeanProcessor();
        return beanProcessor.mappingSelect(this.executeContext, rs, target);
    }

    @Override
    public <T> List<T> select(Class<T> target,Object paras, Object start, long size) {
        SQLExecutor newSqlEx = executeContext.sqlManager.getPageSqlScript(target,executeContext.sqlId);
        Map mapParas = this.beforeExecute(target,paras,false);
        this.executeContext.sqlManager.getDbStyle().getRangeSql().addTemplateRangeParas(mapParas, start, size);
        return (List<T>)newSqlEx.select(target,mapParas);
    }


    @Override
    public long selectCount(Object paras) {
        return this.singleSelect(Long.class,paras);
    }

    @Override
    public int update(Class target,Object object) {
        Map paras = this.beforeExecute(target,object,true);
        SQLResult result = run(paras);
        String sql = result.jdbcSql;
        List<SQLParameter> objs = result.jdbcPara;

        InterceptorContext ctx = this.callInterceptorAsBefore(paras);
        sql = executeContext.sqlResult.jdbcSql;
        objs = executeContext.sqlResult.jdbcPara;
        int rs = 0;

        // 执行jdbc
        Connection conn = null;
        ResultUpdateHolder ruh = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            ruh = this.dbUpdate(conn, sql, objs);
            rs = (Integer) ruh.resultSet;
            executeContext.executeResult = rs;
            this.callInterceptorAsAfter(ctx, rs);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ruh);
        }
        return rs;
    }


    @Override
    public int[] insertBatch(Class<?> target,List<?> list) {

        //与updateBatch区别是需要考虑到id生成
        if (list.size() == 0) {
            return new int[0];
        }
        int[] rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        // 执行jdbc
        InterceptorContext ctx = null;
        try {
            Object firstValue = list.get(0);
            KeyHolder holder = KeyHolder.getKeyHolderByClass(firstValue.getClass());
            for (int k = 0; k < list.size(); k++) {

                Object entity = list.get(k);
                if(entity==null){
                    throw new NullPointerException("列表 "+k+"为空") ;
                }
                this.addParaIfAssignId(entity);
                Map<String, Object> paras = this.beforeExecute(target,entity,true);
                SQLResult result = run(paras);
                List<SQLParameter> objs = result.jdbcPara;
                if (ps == null) {
                    conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
                    if (holder.hasAttr()) {
                        ps = conn.prepareStatement(result.jdbcSql, this.getKeyHolderCols(holder, entity.getClass()));
                    } else {
                        ps = conn.prepareStatement(result.jdbcSql);
                    }
                    ctx = this.callInterceptorAsBefore(paras);
                }

                this.setPreparedStatementPara(ps, objs);
                ps.addBatch();

            }
            rs = ps.executeBatch();
            if (holder.hasAttr()) {
                ResultSet keysSet = ps.getGeneratedKeys();
                String[] attrs = holder.getAttrNames();
                Object[] values = new Object[holder.getAttrNames().length];
                int index = 0;
                while (keysSet.next()) {
                    Object entity = list.get(index);
                    for (int i = 0; i < attrs.length; i++) {
                        Object value = keysSet.getObject(i + 1);
                        BeanKit.setBeanPropertyWithCast(entity, value, attrs[i]);
                    }
                    index++;
                }
                keysSet.close();
            }
            this.executeContext.executeResult = rs;
            this.callInterceptorAsAfter(ctx, rs);

        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ps);
        }
        return rs;
    }


    @Override
    public int[] updateBatch(Class<?> target,List<?> list) {
        if (list.size() == 0) {
            return new int[0];
        }
        Connection conn = null;
        InterceptorContext lastCtx = null;
        int[] jdbcRets = new int[list.size()];
        // 执行jdbc
        try {
            //记录不同sql对应的PreparedStatement
            Map<String, PreparedStatement> batchPs = new HashMap<String, PreparedStatement>();
            //上下文
            Map<String, InterceptorContext> batchCtx = new HashMap<String, InterceptorContext>();
            //不同sql产生的批处理结果，汇总到jdbcRets
            Map<String, List<Integer>> batchRet = new HashMap<String, List<Integer>>();
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            for (int k = 0; k < list.size(); k++) {
                if(list.get(k)==null){
                    throw new NullPointerException("列表 "+k+"参数为空");
                }
                Map<String, Object> paras = this.beforeExecute(target,list.get(k),true);
                SQLResult result = run(paras);
                List<SQLParameter> objs = result.jdbcPara;
                PreparedStatement ps = batchPs.get(result.jdbcSql);
                List<Integer> rets = batchRet.get(result.jdbcSql);
                InterceptorContext ctx = batchCtx.get(result.jdbcSql);
                if (ps == null) {
                    ps = conn.prepareStatement(result.jdbcSql);
                    ctx = new InterceptorContext(executeContext);
                    rets = new ArrayList<Integer>();
                    batchCtx.put(result.jdbcSql, ctx);
                    batchPs.put(result.jdbcSql, ps);
                    batchRet.put(result.jdbcSql, rets);
                }

                this.setPreparedStatementPara(ps, objs);
                ps.addBatch();
                rets.add(k);
            }
            //执行
            for (Map.Entry<String, PreparedStatement> entry : batchPs.entrySet()) {
                PreparedStatement ps = entry.getValue();
                lastCtx = batchCtx.get(entry.getKey());
                List<Integer> rets = batchRet.get(entry.getKey());
                //不调用this.callInterceptorAsBefore()
                for (Interceptor in : executeContext.sqlManager.getInters()) {
                    in.before(lastCtx);
                }
                int[] rs = ps.executeBatch();
                for (int i = 0; i < rs.length; i++) {
                    int realIndex = rets.get(i);
                    jdbcRets[realIndex] = rs[i];
                }
                executeContext.executeResult = rs;
                this.callInterceptorAsAfter(lastCtx, rs);
            }


        } catch (SQLException e) {
            this.callInterceptorAsException(lastCtx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(executeContext, conn);
        }
        return jdbcRets;
    }



    @Override
    public int[] updateBatch(List<?> list){
        return this.updateBatch(null,list);
    }


    @Override
    public <T> T unique(Class<T> target, Object objId) {
        return single(target, objId, true);
    }


    @Override
    public <T> T single(Class<T> target, Object objId) {
        return single(target, objId, false);
    }

    protected <T> T single(Class<T> clazz, Object objId, boolean throwException) {
        executeContext.target = clazz;
        SQLManager sqlManager = executeContext.sqlManager;
        MetadataManager mm = sqlManager.getMetaDataManager();
        TableDesc table = mm.getTable(sqlManager.getNc().getTableName(clazz));
        ClassDesc classDesc = table.genClassDesc(clazz, sqlManager.getNc());
        Map<String, Object> paras = this.beforeExecute(clazz,objId,false);
        this.setIdsParas(classDesc, objId, paras);

        SQLResult result = run(paras);
        String sql = result.jdbcSql;
        List<SQLParameter> objs = result.jdbcPara;
        List<T> resultList = null;
        InterceptorContext ctx = this.callInterceptorAsBefore(paras);
        if (executeContext.executeResult != null) {
            //不用执行数据库
            this.callInterceptorAsAfter(ctx, executeContext.executeResult);
            return (T) executeContext.executeResult;
        }
        sql = executeContext.sqlResult.jdbcSql;
        objs = executeContext.sqlResult.jdbcPara;
        Connection conn = null;
        ResultSetHolder rsh = null;
        try {
            conn = sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, sql, objs);

            T model = null;

            if (executeContext.beanProcessor != null) {
                resultList = executeContext.beanProcessor.toBeanList(executeContext, rsh.resultSet, clazz);
            } else {
                ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
                //单行映射
                RowMapper rowMapper = executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
                //结果集映
                ResultSetMapper resultSetMapper = executeContext.resultMapper != null ? executeContext.resultMapper : classAnnotation.getResultSetMapper();
                //映射方式三选一
                if (resultSetMapper != null) {
                    resultList = resultSetMapper.mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
                } else if (rowMapper != null) {
                    BeanProcessor beanProcessor = this.getBeanProcessor();
                    resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor).handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
                } else {
                    //默认方式
                    resultList = mappingSelect(clazz,rsh.resultSet);
                }
            }

            if (resultList.isEmpty()) {
                if (throwException) {
                    throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集");
                }

            } else {
                model = resultList.get(0);
            }
            executeContext.executeResult = model;
            this.callInterceptorAsAfter(ctx, model);
            return (T)afterBean(model);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(false, conn, rsh);
        }

    }


    @Override
    public boolean existById(Class clazz, Object objId) {
        SQLManager sqlManager = executeContext.sqlManager;
        MetadataManager mm = sqlManager.getMetaDataManager();
        TableDesc table = mm.getTable(sqlManager.getNc().getTableName(clazz));
        ClassDesc classDesc = table.genClassDesc(clazz, sqlManager.getNc());
        Map<String, Object> paras = new HashMap<>();
        this.setIdsParas(classDesc, objId, paras);
        SQLResult result = run(paras);
        String sql = result.jdbcSql;
        List<SQLParameter> objs = result.jdbcPara;

        InterceptorContext ctx = this.callInterceptorAsBefore(paras);
        if (executeContext.executeResult != null) {
            this.callInterceptorAsAfter(ctx, executeContext.executeResult);
            return (Boolean) executeContext.executeResult;
        }
        sql = executeContext.sqlResult.jdbcSql;
        objs = executeContext.sqlResult.jdbcPara;
        Connection conn = null;
        ResultSetHolder rsh = null;
        boolean hasResult = false;
        try {
            conn = sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, sql, objs);
            rsh.resultSet.next();
            int count = rsh.resultSet.getInt(1);
            hasResult = count != 0;
            executeContext.executeResult = count;
            this.callInterceptorAsAfter(ctx, hasResult);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(false, conn, rsh);
        }
        return hasResult;

    }

    @Override
    public int deleteById(Class<?> clazz, Object objId) {
        SQLManager sqlManager = executeContext.sqlManager;
        MetadataManager mm = sqlManager.getMetaDataManager();
        TableDesc table = mm.getTable(sqlManager.getNc().getTableName(clazz));
        ClassDesc classDesc = table.genClassDesc(clazz, sqlManager.getNc());

        Map<String, Object> paras = new HashMap<String, Object>();
        this.setIdsParas(classDesc, objId, paras);

        SQLResult result = run(paras);
        String sql = result.jdbcSql;
        List<SQLParameter> objs = result.jdbcPara;
        InterceptorContext ctx = this.callInterceptorAsBefore(paras);
        sql = executeContext.sqlResult.jdbcSql;
        objs = executeContext.sqlResult.jdbcPara;
        int rs = 0;
        Connection conn = null;
        ResultUpdateHolder ruh = null;
        try {
            conn = sqlManager.getDs().getConn(executeContext, true);
            ruh = this.dbUpdate(conn, sql, objs);
            rs = (Integer) ruh.resultSet;
            executeContext.executeResult = rs;
            this.callInterceptorAsAfter(ctx, rs);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ruh);
        }
        return rs;
    }

    @Override
    public <T> List<T> sqlReadySelect(Class<T> clazz, SQLReady p) {
        SQLResult sqlResult = new SQLResult(p.sql,p.args);
        executeContext.sqlResult = sqlResult;
        List<T> resultList = null;
        InterceptorContext ctx = this.callInterceptorAsBefore(this.beforeExecute(clazz, Arrays.asList(p.getArgs()),false));
        Connection conn = null;
        ResultSetHolder rsh = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, sqlResult.jdbcSql, sqlResult.jdbcPara);
            ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
            //单行映射
            RowMapper rowMapper = executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
            //结果集映
            ResultSetMapper resultSetMapper = executeContext.resultMapper != null ? executeContext.resultMapper : classAnnotation.getResultSetMapper();
            //映射方式三选一
            if (resultSetMapper != null) {
                resultList = resultSetMapper.mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
            } else if (rowMapper != null) {
                BeanProcessor beanProcessor = this.getBeanProcessor();
                resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor).handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
            } else {
                //默认方式
                resultList = mappingSelect(clazz,rsh.resultSet);
            }
            executeContext.executeResult = resultList;
            resultList = (List)this.afterBean(resultList);
            this.callInterceptorAsAfter(ctx, resultList);
            return resultList;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(false, conn, rsh);
        }
    }

    @Override
    public int sqlReadyExecuteUpdate(SQLReady p) {
        SQLResult sqlResult = new SQLResult(p.sql,p.args);
        executeContext.sqlResult = sqlResult;
        InterceptorContext ctx = this.callInterceptorAsBefore(this.beforeExecute(null, Arrays.asList(p.getArgs()),true));
        int rs = 0;
        Connection conn = null;
        ResultUpdateHolder rsh = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            rsh = dbUpdate(conn, sqlResult.jdbcSql, sqlResult.jdbcPara);
            rs = (int) rsh.resultSet;
            executeContext.executeResult = rs;
            this.callInterceptorAsAfter(ctx, rs);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, rsh);
        }
        return rs;
    }

    @Override
    public int[] sqlReadyBatchExecuteUpdate(SQLBatchReady batch) {

        List<Object[]> args = batch.getArgs();
        if (args.isEmpty()) {
            return new int[0];
        }
        InterceptorContext ctx = null;
        Connection conn = null;
        PreparedStatement ps = null;
        int[] rs = null;
        try {

            for (int i = 0; i < args.size(); i++) {
                Object[] jdbcArgs = args.get(i);
                SQLResult sqlResult = new SQLResult(batch.sql,jdbcArgs);
                sqlResult.jdbcSql = batch.sql;
                this.executeContext.sqlResult = sqlResult;
                if (i == 0) {
                    conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
                    ctx = this.callInterceptorAsBefore(this.beforeExecute(null, Arrays.asList(jdbcArgs),true));
                    ps = conn.prepareStatement(sqlResult.jdbcSql);
                }
                this.setPreparedStatementPara(ps, sqlResult.jdbcPara);
                ps.addBatch();

            }
            rs = ps.executeBatch();
            executeContext.executeResult = rs;
            this.callInterceptorAsAfter(ctx, rs);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ps);
        }
        return rs;


    }

    @Override
    public ExecuteContext getExecuteContext() {
        return executeContext;
    }




    protected void setIdsParas(ClassDesc desc, Object obj, Map<String, Object> paras) {
        List<String> idAttrs = desc.getIdAttrs();
        if (idAttrs.size() == 1) {
            paras.put(idAttrs.get(0), obj);
        } else {
            //来自对象id的属性.

            Map<String, Object> map = desc.getIdMethods();
            for (int i = 0; i < idAttrs.size(); i++) {
                String idCol = idAttrs.get(i);
                String idAttr = idAttrs.get(i);
                Method m = (Method) map.get(idAttr);
                try {
                    Object os = m.invoke(obj, new Object[0]);
                    paras.put(idAttr, os);
                } catch (Exception ex) {
                    throw new BeetlSQLException(BeetlSQLException.ID_VALUE_ERROR, "无法设置复合主键:" + idCol, ex);
                }
            }

        }
    }

    protected static boolean isBaseDataType(Class<?> clazz) {
        return BeanKit.isBaseDataType(clazz);

    }


    protected int insert(Object paras, KeyHolder holder) {


        Connection conn = null;
        ResultUpdateHolder ruh = null;
        InterceptorContext ctx = null;
        try {
            this.addParaIfAssignId(paras);
            Map map = this.beforeExecute(paras.getClass(),paras,true);
            SQLResult result = this.run(map);
            String sql = result.jdbcSql;
            List<SQLParameter> jdbcPara = result.jdbcPara;
            ctx = this.callInterceptorAsBefore(map);
            sql = executeContext.sqlResult.jdbcSql;
            jdbcPara = executeContext.sqlResult.jdbcPara;
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            String[] cols = holder.hasAttr() ? this.getKeyHolderCols(holder, paras.getClass()) : null;
            ruh = this.dbUpdateWithHolder(conn, sql, jdbcPara, cols);
            int ret = (Integer) ruh.resultSet;
            this.handleHolder(ruh.statement, holder);
            executeContext.executeResult = ret;
            this.callInterceptorAsAfter(ctx, ret);
            return ret;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ruh);
        }
    }

    protected void clean(boolean isUpdate, Connection conn, PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    protected void clean(boolean isUpdate, Connection conn, Closeable closeable) {

        try {
            if (closeable != null) {
                closeable.close();
            }

            closeConnection(conn, isUpdate);

        } catch (SQLException e) {
            // ignore
        }
    }

    protected void closeConnection(Connection conn, boolean isUpdate) {
        if (!executeContext.sqlManager.getDs().isTransaction()) {
            try {

                if (conn != null) {
                    // colse 不一定能保证能自动commit
                    if (isUpdate && !conn.getAutoCommit()) {

                        conn.commit();
                    }

                    conn.close();
                }
            } catch (SQLException e) {
                throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
            }

        }
    }

    protected ResultSetHolder dbQuery(Connection conn, String sql, List<SQLParameter> jdbcPara) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        this.setPreparedStatementPara(ps, jdbcPara);
        ResultSet rs = ps.executeQuery();

        return new ResultSetHolder(ps, rs);
    }

    protected ResultUpdateHolder dbUpdate(Connection conn, String sql, List<SQLParameter> jdbcPara) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        this.setPreparedStatementPara(ps, jdbcPara);
        int result = ps.executeUpdate();
        return new ResultUpdateHolder(ps, result);
    }

    protected ResultUpdateHolder dbUpdateWithHolder(Connection conn, String sql, List<SQLParameter> jdbcPara, String[] cols) throws SQLException {
        PreparedStatement ps = null;
        if (cols != null) {
            ps = conn.prepareStatement(sql, cols);
        } else {
            ps = conn.prepareStatement(sql);
        }
        this.setPreparedStatementPara(ps, jdbcPara);
        int result = ps.executeUpdate();

        return new ResultUpdateHolder(ps, result);
    }

    static interface Closeable {
        public void close() throws SQLException;
    }

    public static class ResultSetHolder implements Closeable {
        Statement statement;
        ResultSet resultSet;

        public ResultSetHolder(Statement statement, ResultSet resultSet) {
            this.statement = statement;
            this.resultSet = resultSet;
        }

        @Override
        public void close() throws SQLException {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
        }
    }

    public static class ResultUpdateHolder implements Closeable {
        Statement statement;
        Object resultSet;

        public ResultUpdateHolder(Statement statement, Object result) {
            this.statement = statement;
            this.resultSet = result;
        }

        @Override
        public void close() throws SQLException {
            if (statement != null) {
                statement.close();
            }
        }
    }


    protected void clean(ExecuteContext executeContext, Connection conn) {
        this.clean(true, conn, (Closeable) null);
    }

    /**
     * 处理插入数据库后，数据库自动生成值赋值给Holder
     *
     * @param ps
     * @param holder
     * @throws SQLException
     */
    protected void handleHolder(Statement ps, KeyHolder holder) throws SQLException {
        if (!holder.hasAttr()) {
            return;
        }
        ResultSet rs = ps.getGeneratedKeys();
        Object[] values = new Object[holder.getAttrNames().length];
        while (rs.next()) {
            for (int i = 0; i < values.length; i++) {
                values[i] = rs.getObject(i + 1);
            }
            break;
        }
        holder.setValues(values);
        rs.close();

    }

    /**
     * 把{@code KeyHolder}的值赋值回Bean
     *
     * @param holder
     * @param paras
     */
    protected void assignKeyHolder(KeyHolder holder, Object paras) {
        if(paras instanceof  Map){
            return ;
        }
        String[] attrs = holder.getAttrNames();
        Object[] values = holder.getValues();
        int len = attrs.length;
        for (int i = 0; i < len; i++) {
            BeanKit.setBeanPropertyWithCast(paras, values[i], attrs[i]);
        }

    }


    private void addParaIfAssignId(Object obj) {
        if (obj instanceof Map) {
            return;
        }
        if (obj == null) {
            return;
        }

        if(!(executeContext.sqlSource instanceof  SQLTableSource)){
            //sqlId
            return ;
        }
        Class clz = executeContext.target;
        SQLTableSource tableSource = (SQLTableSource) executeContext.sqlSource;
        if (tableSource.getIdType() == DBType.ID_ASSIGN && tableSource.getAssignIds() != null) {
            Map<String, AssignID> ids = tableSource.getAssignIds();
            for (Map.Entry<String, AssignID> entry : ids.entrySet()) {
                String attrName = entry.getKey();
                Object value = BeanKit.getBeanProperty(obj, attrName);
                // 已经有值的列尊重调用者设置的值，@lidaoguang
                // 严格判断 null 和 empty 的 value，支持 ID 类型为 String 或者 Char 类型的情况 @larrykoo
                if (!StringKit.isNullOrEmpty(value)) {
                    continue;
                }
                AssignID assignId = entry.getValue();
                String algorithm = assignId.value();
                if(StringKit.isEmpty(algorithm)){
					throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND,"使用@Assign未指定算法，需要显示的赋值给 "+attrName);
				}
                String param = assignId.param();
                Object o = executeContext.sqlManager.getAssignIdByIdAutonGen(algorithm, param, tableSource.getTableDesc().getName());
                BeanKit.setBeanProperty(obj, o, attrName);

            }

        }

    }

    protected String[] getKeyHolderCols(KeyHolder holder, Class target) {
        String[] attrs = holder.getAttrNames();
        String[] cols = new String[attrs.length];
        NameConversion nc = executeContext.sqlManager.getNc();
        for (int i = 0; i < attrs.length; i++) {
            cols[i] = nc.getColName(target, attrs[i]);
        }
        return cols;

    }


    protected void setPreparedStatementPara(PreparedStatement ps, List<SQLParameter> objs) throws SQLException {
        if (objs.isEmpty()) {
            return;
        }
        BeanProcessor beanProcessor = this.getBeanProcessor();
        beanProcessor.setPreparedStatementPara(executeContext, ps, objs);

    }


    private BeanProcessor getBeanProcessor() {
        return executeContext.sqlManager.getDefaultBeanProcessors();
    }


    /**
     * 封装传入的参数为Map结构，在Beetl中执行
     *
     * @param paras
     * @return
     */
    @Override
    public Map beforeExecute(Class target, Object paras,boolean isUpdate) {
        executeContext.target = target;
        executeContext.inputParas = paras;
		executeContext.isUpdate = isUpdate;


        if(paras==null){
            Map map =  new HashMap() ;
            addMoreParasFromTarget(target,map);
            return map;
        }
        if(paras instanceof Map){
            Map map = (Map)paras;
            addMoreParasFromTarget(target,map);
            return map;
        }

        Map map = new HashMap();

        map.put("_root", paras);
        Class parasType = paras.getClass();
        if(isBaseDataType(paras.getClass())){
            return map;
        }


        //处理入参是否需要变化
        ClassAnnotation an = ClassAnnotation.getClassAnnotation(parasType);
        if(an.isContainExtAnnotation()){
            ClassExtAnnotation ext = an.getExtAnnotation();
            if(ext.hasAttributeExt()){
                Map<String, AttributeConvert> attrMap =ext.getAttributeConvertMap();
                for(Map.Entry<String, AttributeConvert> entry:attrMap.entrySet()){
                    String attr = entry.getKey();
                    AttributeConvert convert = entry.getValue();
                    Object newValue = convert.toDb(executeContext,parasType,attr,paras);
                    //相对于"_root"，模板引擎会先找map下的属性
                    map.put(attr,newValue);
                }
            }
            if(ext.hasEntityExt()){
                BeanConvert beanConvert =  ext.getBeanConvert();
                if(beanConvert!=null){
                    //得到新的paras
                    Object obj = beanConvert.before(executeContext,paras,ext.getBeanConvertAnnotation());
                    if(obj instanceof  Map){
                        map.putAll((Map)obj);
                    }else{
                        map.put("_root",obj);
                    }
                }
            }
        }
        addMoreParasFromTarget(target,map);



        return map;
    }

    protected  void addMoreParasFromTarget(Class target,Map map){
        if(target==null) {
            return ;
        }
        ClassAnnotation targetClassAnnotation = ClassAnnotation.getClassAnnotation(target);
        if(targetClassAnnotation.isContainExtAnnotation()){
            ClassExtAnnotation targetExtClassAnnotation = targetClassAnnotation.getExtAnnotation();
            Annotation annotation = targetExtClassAnnotation.getAdditionalAnnotation();
            if(annotation!=null){
                TargetAdditional targetAdditional = targetExtClassAnnotation.getTargetAdditional();
                Map moreParas = targetAdditional.getAdditional(executeContext,annotation);
                if(moreParas!=null&&!moreParas.isEmpty()){
                    map.putAll(moreParas);
                }

            }
        }


    }


    @Override
    public SQLResult run(Map<String, Object> parasMap) {
        return this.run(parasMap, null);
    }

    /**
     * sql模板里调用，比如use
     *
     * @param parasMap
     * @param ctx
     * @return
     */
    @Override
    public SQLResult run(Map<String, Object> parasMap, TemplateContext ctx) {
        SQLTemplateEngine gt = executeContext.sqlManager.sqlTemplateEngine;
        SQLTemplate t = null;
        ExecuteContext parentExecuteContext = ctx == null ? null : (ExecuteContext) ctx.getVar("_executeContext");

        if (parentExecuteContext != null) {
            t = gt.getSqlTemplate(executeContext.sqlId, ctx);
        } else {
            t = gt.getSqlTemplate(executeContext.sqlId);
        }

        List<SQLParameter> jdbcPara = new LinkedList<SQLParameter>();
        if (parasMap != null) {
            for (Map.Entry<String, Object> entry : parasMap.entrySet()) {
                t.setPara(entry.getKey(), entry.getValue());
            }
        }

        t.setPara("_paras", jdbcPara);
        t.setPara("_executeContext", executeContext);
        String jdbcSql = t.render();
        executeContext.sqlResult.jdbcSql = jdbcSql;
        executeContext.sqlResult.jdbcPara = jdbcPara;

        SQLResult result = new SQLResult();
        result.jdbcSql = jdbcSql;
        result.jdbcPara = jdbcPara;
        return result;
    }

    protected InterceptorContext callInterceptorAsBefore(Map<String, Object> inputParas) {

        InterceptorContext ctx = new InterceptorContext(executeContext);
        for (Interceptor in : executeContext.sqlManager.inters) {
            in.before(ctx);
        }

        return ctx;

    }

    protected void callInterceptorAsAfter(InterceptorContext ctx, Object result) {

        SQLManager sqlManager = executeContext.sqlManager;

        if (sqlManager.inters == null) {
            return;
        }

        for (Interceptor in : executeContext.sqlManager.inters) {
            in.after(ctx);
        }
        return;
    }

    /**
     * bean后处理
     *
     * @param result
     */
    protected Object afterBean(Object result) {
    	if(result==null){
    		return null;
		}
        Class target = this.executeContext.target;
        if (target == null) {
            return result;
        }
        if (target == Map.class) {
            return result;
        }
        if (isBaseDataType(target)) {
            return result;
        }
        ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(target);
        ClassExtAnnotation extAnnotation = classAnnotation.getExtAnnotation();
        if (extAnnotation!= null && extAnnotation.getBeanConvert() != null) {
            BeanConvert convert = extAnnotation.getBeanConvert();
            if(convert!=null){
                Annotation annotation = extAnnotation.getBeanConvertAnnotation();
                if(result instanceof  List){
                    List resultList = (List)result;
                    resultList.stream().forEach(obj -> convert.after(executeContext, obj, annotation));
                }else{
                    convert.after(executeContext,result,annotation);
                }

            }
        }

        BeanFetch beanFetch = classAnnotation.getBeanFetch();
        if(beanFetch!=null){
            if(result instanceof List ){
                List resultList = (List)result;
                beanFetch.fetchMore(executeContext,resultList,classAnnotation.getBeanFetchAnnotation());
            }else{
                List list = new ArrayList(1);
                list.add(result);
                beanFetch.fetchMore(executeContext,list,classAnnotation.getBeanFetchAnnotation());
                result = list.get(0);
            }

        }

        return result;

    }




    protected void callInterceptorAsAfter(InterceptorContext ctx) {
        callInterceptorAsAfter(ctx, null);
    }

    private void callInterceptorAsException(InterceptorContext ctx, Exception ex) {
        if (ctx == null) {
            return;
        }
        SQLManager sqlManager = executeContext.sqlManager;
        if (sqlManager.inters == null) {
            return;
        }

        for (Interceptor in : sqlManager.inters) {
            in.exception(ctx, ex);
        }
        return;
    }


}
