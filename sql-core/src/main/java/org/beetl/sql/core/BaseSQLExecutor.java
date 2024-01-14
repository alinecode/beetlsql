package org.beetl.sql.core;

import lombok.Data;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.annotation.builder.BeanConvert;
import org.beetl.sql.annotation.builder.TargetAdditional;
import org.beetl.sql.annotation.entity.AssignID;
import org.beetl.sql.clazz.*;
import org.beetl.sql.clazz.kit.*;
import org.beetl.sql.core.call.CallArg;
import org.beetl.sql.core.call.CallReady;
import org.beetl.sql.core.call.InArg;
import org.beetl.sql.core.call.OutArg;
import org.beetl.sql.core.db.DBType;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.engine.template.SQLTemplate;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.engine.template.TemplateContext;
import org.beetl.sql.core.mapping.*;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.meta.MetadataManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * 面向传统数据库的 sql 执行引擎
 *
 * @author xiandafu
 * @see BaseStatementOnlySQLExecutor
 */
public class BaseSQLExecutor implements SQLExecutor {

    ExecuteContext executeContext;

    public BaseSQLExecutor(ExecuteContext executeContext) {
        this.executeContext = executeContext;
    }

    protected static boolean isBaseDataType(Class<?> clazz) {
        return BeanKit.isBaseDataType(clazz);
    }

    @Override
    public int insert(Class clazz, Object paras) {
        KeyHolder holder = paras instanceof Map ? KeyHolder.empty : KeyHolder.getKeyHolderByClass(paras);
        int ret = insert(paras, holder);
        this.assignKeyHolder(holder, paras);
        return ret;
    }

    @Override
    public List<Object[]> insert(Class target, Object paras, String[] cols) {

        Connection conn = null;
        ResultUpdateHolder ruh = null;
        InterceptorContext ctx = null;
        try {
            this.addParaIfAssignId(paras);
            Map map = this.beforeExecute(target, paras, true);
            SQLResult result = this.run(map);
            ctx = this.callInterceptorAsBefore(map);
			String sql  = executeContext.sqlResult.jdbcSql;
			List<SQLParameter> jdbcPara = executeContext.sqlResult.jdbcPara;
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            ruh = dbUpdateWithHolder(conn, sql, jdbcPara, cols);

            // 如果数据不支持getGeneratedKeys就直接返回，自增长值为null
            if (!executeContext.sqlManager.getDbStyle().generatedKeysSupport()) {
                List<Object[]> keyHolders = new ArrayList<>((Integer)ruh.getResultSet());

                this.callInterceptorAsAfter(ctx, ruh.resultSet);
                return keyHolders;
            }

			ResultSet rs = ruh.statement.getGeneratedKeys();
			NameConversion nc = this.executeContext.sqlManager.getNc();

			List<Object[]> keyHolders = new ArrayList<>((Integer)ruh.getResultSet());
			while (rs.next()) {
				Object[] values = new Object[cols.length];
				for(int i=0;i<cols.length;i++){
					values[i] = rs.getObject(i+1);
				}
				keyHolders.add(values);
			}
			rs.close();
            this.callInterceptorAsAfter(ctx, ruh.resultSet);
            return keyHolders;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ruh);
        }
    }

    @Override
    public <T> T singleSelect(Class<T> target, Object paras) {
        Map<String, Object> map = this.beforeExecute(target, paras, false);
        return this.selectSingle(map, target);
    }

    @Override
    public <T> T selectUnique(Class<T> target, Object paras) {

        Map map = this.beforeExecute(target, paras, false);
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
        Map map = this.beforeExecute(clazz, paras, false);
        return this.select(clazz, map);
    }

    protected <T> T selectSingle(Map<String, Object> map, Class<T> target) {
        // 是否翻页来限定，一般调用这个方法，都可能是因为只有一条？
        List<T> result = select(target, map);
        if (!result.isEmpty()) {
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


            if (executeContext.customizedBeanProcessor != null) {
                resultList = executeContext.customizedBeanProcessor.toBeanList(executeContext, rsh.resultSet, clazz);

            } else {
                ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
                //单行映射
                RowMapper rowMapper =
                        executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
                //结果集映
                ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
                        executeContext.resultMapper :
                        classAnnotation.getResultSetMapper();
                //映射方式三选一
                if (resultSetMapper != null) {
                    resultList = resultSetMapper
                            .mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
                } else if (rowMapper != null) {
                    BeanProcessor beanProcessor = this.getBeanProcessor();
                    resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor)
                            .handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
                } else {
                    //默认方式
                    resultList = mappingSelect(clazz, rsh.resultSet);
                }
            }


            this.executeContext.executeResult = resultList;
            this.callInterceptorAsAfter(ctx, resultList);
            resultList = (List) afterBean(resultList);
            return resultList;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(false, conn, rsh);
        }

    }

    @Override
    public <T> List<T> mappingSelect(Class<T> target, ResultSet rs) throws SQLException {
        BeanProcessor beanProcessor = this.getBeanProcessor();
        return beanProcessor.mappingSelect(this.executeContext, rs, target);
    }

    @Override
    public <T> List<T> select(Class<T> target, Object paras, Object start, long size) {
        SQLExecutor newSqlEx = executeContext.sqlManager.getPageSqlScript(target, executeContext.sqlId);
        Map mapParas = this.beforeExecute(target, paras, false);
        this.executeContext.sqlManager.getDbStyle().getRangeSql().addTemplateRangeParas(mapParas, start, size);
        return newSqlEx.select(target, mapParas);
    }

    @Override
    public long selectCount(Object paras) {
        return this.singleSelect(Long.class, paras);
    }

    @Override
    public int update(Class target, Object object) {
        Map paras = this.beforeExecute(target, object, true);
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
    public int[] insertBatch(Class<?> target, List<?> list) {

        //与updateBatch区别是需要考虑到id生成
        if (list.size() == 0) {
            return new int[0];
        }
        int[] rs = null;
        PreparedStatement ps = null;
        Connection conn = null;
        // 执行jdbc
        InterceptorContext ctx = new InterceptorContext(executeContext);
		GroupBatchExecutor groupBatchExecutor = new GroupBatchExecutor();
        try {
            Object firstValue = list.get(0);
            KeyHolder holder = KeyHolder.getKeyHolderByClass(firstValue);
            for (int k = 0; k < list.size(); k++) {

                Object entity = list.get(k);
                if (entity == null) {
                    throw new NullPointerException("列表 " + k + "为空");
                }
                this.addParaIfAssignId(entity);
                Map<String, Object> paras = this.beforeExecute(target, entity, true);
                SQLResult result = run(paras);
                List<SQLParameter> objs = result.jdbcPara;
                if (ps == null) {
                    conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
                    if (holder.hasAttr()) {
                        ps = conn.prepareStatement(result.jdbcSql, this.getKeyHolderCols(holder, entity.getClass()));
                    } else {
                        ps = conn.prepareStatement(result.jdbcSql);
                    }
                    this.applyStatementSetting(executeContext, conn, ps);
                    ctx = this.callInterceptorAsBefore(paras);
                }

                this.setPreparedStatementPara(ps, objs);
                ps.addBatch();
				groupBatchExecutor.addSql(result,ps);

            }
			rs = groupBatchExecutor.executeBatch(executeContext,ctx,executeContext.sqlManager.isBatchLogOneByOne());
            if (executeContext.sqlManager.getDbStyle().batchGeneratedKeysSupport()) {
                if (holder.hasAttr()) {
                    ResultSet keysSet = ps.getGeneratedKeys();
                    String[] attrs = holder.getAttrNames();
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
            }


        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ps);
        }
        return rs;
    }

    @Override
    public int[] updateBatch(Class<?> target, List<?> list) {
        if (list.isEmpty()) {
            return new int[0];
        }
        Connection conn = null;
		InterceptorContext ctx = new InterceptorContext(executeContext);
        try {
			GroupBatchExecutor groupBatchExecutor = new GroupBatchExecutor();
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            for (int k = 0; k < list.size(); k++) {
                if (list.get(k) == null) {
                    throw new NullPointerException("列表 " + k + "参数为空");
                }
				addParam2BatchExecutor(target, list.get(k), conn, groupBatchExecutor, true);
            }
			return groupBatchExecutor.executeBatch(executeContext,ctx,executeContext.sqlManager.isBatchLogOneByOne());
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(executeContext, conn);
        }

    }

	@Override
	public int[] executeBatch(List<SqlIdWithParam> list, Integer batchSize) {
		if (ListUtil.isEmpty(list)) {
			return new int[0];
		}
		//批处理数量默认1000
		batchSize = batchSize == null ? 1000 : batchSize;
		Connection conn = null;
		InterceptorContext ctx = new InterceptorContext(executeContext);
		try {
			GroupBatchExecutor groupBatchExecutor = new GroupBatchExecutor();
			conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
			int[] rows = new int[list.size()];
			//分批执行
			List<List<SqlIdWithParam>> batchList = ListUtil.partition(list, batchSize);
			for (List<SqlIdWithParam> batch : batchList) {
				for (int k = 0; k < batch.size(); k++) {
					SqlIdWithParam item = batch.get(k);
					if (item == null) {
						throw new NullPointerException("列表 " + k + "参数为空");
					}
					SQLSource sqlSource = executeContext.sqlManager.getSqlLoader().querySQL(item.getSqlId());
					executeContext.initSQLSource(sqlSource);
					Object sqlParams = item.getSqlParam();
					boolean isUpdate = sqlSource.getSqlType().isUpdate();
					//sql参数为集合
					if(sqlParams instanceof List){
						List<Object> sqlParamList = (List<Object>)sqlParams;
                        for (Object param : sqlParamList) {
							addParam2BatchExecutor(null, param, conn, groupBatchExecutor,isUpdate);
                        }
                    }else {
						addParam2BatchExecutor(null, item.getSqlParam(), conn, groupBatchExecutor,isUpdate);
					}
				}
				int[] group = groupBatchExecutor.executeBatch(executeContext, ctx, executeContext.sqlManager.isBatchLogOneByOne());
				rows = ArrayKit.concatAll(rows, group);
			}
			return rows;
		} catch (SQLException e) {
			this.callInterceptorAsException(ctx, e);
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(executeContext, conn);
		}
	}

	private void addParam2BatchExecutor(Class<?> target, Object param, Connection conn, GroupBatchExecutor groupBatchExecutor, boolean isUpdate) throws SQLException {
		Map<String, Object> paras = this.beforeExecute(target, param, isUpdate);
		SQLResult result = run(paras);
		PreparedStatement ps = groupBatchExecutor.containSql(result.jdbcSql);
		if (ps == null) {
			ps = conn.prepareStatement(result.jdbcSql);
		}
		this.applyStatementSetting(executeContext, conn, ps);
		this.setPreparedStatementPara(ps, result.jdbcPara);
		ps.addBatch();
		groupBatchExecutor.addSql(result, ps);
	}

	static class GroupBatchExecutor {
		Map<String, PreparedStatement> batchPs = new HashMap<>();

		//记录不同sql对应的参数，这里的SQLParameter是特殊的SQLParameter。
		Map<String, List<SQLParameter>> batchParameter = new HashMap<>();

		List<Integer> allRet = new ArrayList<>();


		public void addSql(SQLResult result,PreparedStatement ps){
			String sql = result.jdbcSql;
			if(!batchPs.keySet().contains(sql)){

				batchPs.put(sql, ps);
				batchParameter.put(sql, new ArrayList<SQLParameter>());
			}

			SQLParameter specialParameter = new SQLParameter(result.jdbcPara);
			batchParameter.get(result.jdbcSql).add(specialParameter);
			return ;
		}
		public PreparedStatement containSql(String sql){
			return batchPs.get(sql);
		}

		public int[] executeBatch(ExecuteContext executeContext,InterceptorContext ctx,boolean singleUpdate ) throws SQLException{

			for (Map.Entry<String, PreparedStatement> entry : batchPs.entrySet()) {
				PreparedStatement ps = entry.getValue();
				String sql = entry.getKey();
				executeContext.sqlResult.jdbcSql = sql;
				if(singleUpdate){
					//模拟单条打印,有些系统需要跟踪每个sql语句和执行参数,这会导致较多的输出日志
					int[] rs = new int[0];
					List<SQLParameter> list = batchParameter.get(sql);
					for(int i=0;i<list.size();i++){
						SQLParameter sqlParameter = list.get(i);
						executeContext.sqlResult.jdbcPara = (List<SQLParameter>)sqlParameter.value;
						for (Interceptor in : executeContext.sqlManager.getInters()) {
							in.before(ctx);
						}
						if(rs.length==0){
							rs = ps.executeBatch();
							addRet(rs);

						}

						executeContext.executeResult = rs[i];
						for (Interceptor in : executeContext.sqlManager.inters) {
							in.after(ctx);
						}
					}

				}else{
					//只打印第一组参数和执行结果，大多数情况如此
					SQLParameter sqlParameter = batchParameter.get(sql).get(0);
					List<SQLParameter> sqlParameters = (List<SQLParameter>)sqlParameter.value;
					executeContext.sqlResult.jdbcPara = (List<SQLParameter>)sqlParameter.value;
					for (Interceptor in : executeContext.sqlManager.getInters()) {
						in.before(ctx);
					}
					int[] rs = ps.executeBatch();
					addRet(rs);
					executeContext.executeResult = rs[0];
					for (Interceptor in : executeContext.sqlManager.inters) {
						in.after(ctx);
					}
				}



			}
			return allRet();
		}

		public void addRet(int[] rets){
			for(int i=0;i<rets.length;i++){
				allRet.add(rets[i]);
			}
		}

		public int[] allRet(){
			int[] ints = allRet.stream().mapToInt(Integer::valueOf).toArray();
			return ints;
		}
	}


    @Override
    public int[] updateBatch(List<?> list) {
        return this.updateBatch(null, list);
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
        Map<String, Object> paras = this.beforeExecute(clazz, objId, false);
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

            if (executeContext.customizedBeanProcessor != null) {
                resultList = executeContext.customizedBeanProcessor.toBeanList(executeContext, rsh.resultSet, clazz);
            } else {
                ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
                //单行映射
                RowMapper rowMapper =
                        executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
                //结果集映
                ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
                        executeContext.resultMapper :
                        classAnnotation.getResultSetMapper();
                //映射方式三选一
                if (resultSetMapper != null) {
                    resultList = resultSetMapper
                            .mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
                } else if (rowMapper != null) {
                    BeanProcessor beanProcessor = this.getBeanProcessor();
                    resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor)
                            .handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
                } else {
                    //默认方式
                    resultList = mappingSelect(clazz, rsh.resultSet);
                }
            }

            if (resultList.isEmpty()) {
                if (throwException) {
                    throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集 " + objId);
                }

            } else {
                model = resultList.get(0);
            }
            executeContext.executeResult = model;
            this.callInterceptorAsAfter(ctx, model);
            return (T) afterBean(model);
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } catch (BeetlSQLException beetlSQLException) {
            //仍然把sql打印出来
            this.callInterceptorAsException(ctx, beetlSQLException);
            throw beetlSQLException;
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

        Map<String, Object> paras = new HashMap<>();
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
        SQLResult sqlResult = new SQLResult(p.sql, p.args);
        executeContext.sqlResult = sqlResult;
        List<T> resultList = null;
        InterceptorContext ctx = this
                .callInterceptorAsBefore(this.beforeExecute(clazz, Arrays.asList(p.getArgs()), false));
        Connection conn = null;
        ResultSetHolder rsh = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, sqlResult.jdbcSql, sqlResult.jdbcPara);
            ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
            //单行映射
            RowMapper rowMapper =
                    executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
            //结果集映射
            ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
                    executeContext.resultMapper :
                    classAnnotation.getResultSetMapper();
            //映射方式三选一
            if (resultSetMapper != null) {
                resultList = resultSetMapper
                        .mapping(executeContext, clazz, rsh.resultSet, classAnnotation.getMapperConfig());
            } else if (rowMapper != null) {
                BeanProcessor beanProcessor = this.getBeanProcessor();
                resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor)
                        .handleResultSet(executeContext, rsh.resultSet, clazz, classAnnotation.getMapperConfig());
            } else {
                //默认方式
                resultList = mappingSelect(clazz, rsh.resultSet);
            }
            executeContext.executeResult = resultList;
            resultList = (List) this.afterBean(resultList);
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
    public <T> StreamData<T> streamExecute(Class<T> clazz, SQLReady p) {
        SQLResult sqlResult = new SQLResult(p.sql, p.args);
        executeContext.sqlResult = sqlResult;
        List<T> resultList = null;
        Connection conn = null;
        ResultSetHolder rsh = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, sqlResult.jdbcSql, sqlResult.jdbcPara);
            ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
            //单行映射
            RowMapper rowMapper =
                    executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
            //结果集映
            ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
                    executeContext.resultMapper :
                    classAnnotation.getResultSetMapper();
            //映射方式三选一
            if (resultSetMapper != null) {
                throw new UnsupportedOperationException("stream查询不支持ResultSetMapper");
            }
            StreamData data = new StreamData(rsh.resultSet, this.executeContext, clazz);
            if (rowMapper != null) {
                data.setRowMapper(rowMapper, classAnnotation.getMapperConfig());
            }
            //不 close 数据库链接，期待事物上下文结束后关闭链接，参考StreamData.foreach方法
            return data;
        } catch (SQLException e) {
            clean(false, conn, rsh);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        }
    }

    @Override
    public <T> StreamData<T> stream(Class<T> clazz, Object obj) {
        Map paras = this.beforeExecute(clazz, obj, false);
        SQLResult result = run(paras);
        ResultSetHolder rsh = null;
        Connection conn = null;
        try {
            conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
            rsh = dbQuery(conn, result.jdbcSql, result.jdbcPara);
            ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
            //单行映射
            RowMapper rowMapper =
                    executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
            //结果集映
            ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
                    executeContext.resultMapper :
                    classAnnotation.getResultSetMapper();
            //映射方式三选一
            if (resultSetMapper != null) {
                throw new UnsupportedOperationException("stream查询不支持ResultSetMapper");
            }
            StreamData data = new StreamData(rsh.resultSet, this.executeContext, clazz);
            if (rowMapper != null) {
                data.setRowMapper(rowMapper, classAnnotation.getMapperConfig());
            }
            //不 close 数据库链接，期待事物上下文结束后关闭链接，参考StreamData.foreach方法
            return data;
        } catch (SQLException e) {
            clean(false, conn, rsh);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        }
    }

    @Override
    public int sqlReadyExecuteUpdate(SQLReady p) {
        SQLResult sqlResult = new SQLResult(p.sql, p.args);
        executeContext.sqlResult = sqlResult;
        InterceptorContext ctx = this
                .callInterceptorAsBefore(this.beforeExecute(null, Arrays.asList(p.getArgs()), true));
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
        InterceptorContext ctx = new InterceptorContext(executeContext);
        Connection conn = null;
        PreparedStatement ps = null;
		GroupBatchExecutor groupBatchExecutor = new GroupBatchExecutor();
        try {
            for (int i = 0; i < args.size(); i++) {
                Object[] jdbcArgs = args.get(i);
                SQLResult sqlResult = new SQLResult(batch.sql, jdbcArgs);
                sqlResult.jdbcSql = batch.sql;
                this.executeContext.sqlResult = sqlResult;
                if (i == 0) {
                    conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
                    ctx = this.callInterceptorAsBefore(this.beforeExecute(null, Arrays.asList(jdbcArgs), true));
                    ps = conn.prepareStatement(sqlResult.jdbcSql);
                }
                this.setPreparedStatementPara(ps, sqlResult.jdbcPara);
                ps.addBatch();
				groupBatchExecutor.addSql(sqlResult, ps);

            }

			int[] ret = groupBatchExecutor.executeBatch(executeContext,ctx,executeContext.sqlManager.isBatchLogOneByOne());
			return ret;
        } catch (SQLException e) {
            this.callInterceptorAsException(ctx, e);
            throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
        } finally {
            clean(true, conn, ps);
        }



    }

	@Override
	public int executeCall(CallReady callReady) {

		Connection conn = null;
		CallableStatement  call  = null;
		try {
			conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
			call = conn.prepareCall(callReady.getSql());
			configCall(call,callReady);

			int ret  = call.executeUpdate();
			//处理out部分
			for(CallArg arg:callReady.getArgs()){
				if(arg instanceof OutArg) {
					OutArg outArg = (OutArg) arg;
					Object value = call.getObject(outArg.getIndex(),outArg.getOutType());
					outArg.setOutValue(value);
				}
			}
			return ret;

		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, call);
		}


	}

	@Override
	public <T> List<T> executeCall(CallReady callReady, Class<T> clazz) {
		Connection conn = null;
		CallableStatement  call  = null;
		BeanProcessor beanProcessor = this.getBeanProcessor();
		try {
			//存储过程认为都是update
			conn = executeContext.sqlManager.getDs().getConn(executeContext, false);
			call = conn.prepareCall(callReady.getSql());

			configCall(call,callReady);

			ResultSet ret  = call.executeQuery();
			List<T> resultList = null;
			ClassAnnotation classAnnotation = ClassAnnotation.getClassAnnotation(clazz);
			//单行映射
			RowMapper rowMapper =
					executeContext.rowMapper != null ? executeContext.rowMapper : classAnnotation.getRowMapper();
			//结果集映射
			ResultSetMapper resultSetMapper = executeContext.resultMapper != null ?
					executeContext.resultMapper :
					classAnnotation.getResultSetMapper();
			//映射方式三选一
			if (resultSetMapper != null) {
				resultList = resultSetMapper
						.mapping(executeContext, clazz, ret, classAnnotation.getMapperConfig());
			} else if (rowMapper != null) {

				resultList = new RowMapperResultSetExt<T>(rowMapper, beanProcessor)
						.handleResultSet(executeContext, ret, clazz, classAnnotation.getMapperConfig());
			} else {
				//默认方式
				resultList = mappingSelect(clazz, ret);
			}
			executeContext.executeResult = resultList;
			resultList = (List) this.afterBean(resultList);
			//处理out部分
			for(CallArg arg:callReady.getArgs()){
				if(arg instanceof OutArg) {
					OutArg outArg = (OutArg) arg;
					Object value = call.getObject(outArg.getIndex(),outArg.getOutType());
					outArg.setOutValue(value);
				}
			}

			return resultList;

		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			clean(true, conn, call);
		}

	}

	protected  void configCall(CallableStatement  call,CallReady callReady) throws SQLException {
		List<CallArg> list = callReady.getArgs();
		BeanProcessor beanProcessor = this.getBeanProcessor();

		for(CallArg arg:list){
			if(arg instanceof InArg){
				InArg inArg  = ((InArg) arg);
				if(inArg.hasJdbcType()){
					call.setObject(arg.getIndex(),inArg,inArg.getJdbcType());
				}else{

					call.setObject(arg.getIndex(),inArg.getArg());
				}

			}else{
				OutArg outArg = (OutArg)arg;
				if(outArg.hasJdbcType()){
					call.registerOutParameter(arg.getIndex(),outArg.getJdbcType());
				}else{
					JavaSqlTypeHandler sqlTypeHandler = beanProcessor.getHandler(outArg.getOutType());
					if(sqlTypeHandler==null){
						throw new UnsupportedOperationException("不能根据"+outArg.getOutType()+" 判断出参类型，需要指示jdbc type"+arg.getIndex());
					}
					call.registerOutParameter(arg.getIndex(),sqlTypeHandler.jdbcType());
				}
			}
		}

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
                    Object os = m.invoke(obj);
                    paras.put(idAttr, os);
                } catch (Exception ex) {
                    throw new BeetlSQLException(BeetlSQLException.ID_VALUE_ERROR, "无法设置复合主键:" + idCol, ex);
                }
            }

        }
    }

    protected int insert(Object paras, KeyHolder holder) {
        Connection conn = null;
        ResultUpdateHolder ruh = null;
        InterceptorContext ctx = null;
        try {
            this.addParaIfAssignId(paras);
            Map map = this.beforeExecute(paras.getClass(), paras, true);
            SQLResult result = this.run(map);
            String sql = result.jdbcSql;
            List<SQLParameter> jdbcPara = result.jdbcPara;
            ctx = this.callInterceptorAsBefore(map);
            sql = executeContext.sqlResult.jdbcSql;
            jdbcPara = executeContext.sqlResult.jdbcPara;
            conn = executeContext.sqlManager.getDs().getConn(executeContext, true);
            boolean holderHasAttr = holder.hasAttr();
            String[] cols = holderHasAttr ? this.getKeyHolderCols(holder, paras.getClass()) : null;
            ruh = this.dbUpdateWithHolder(conn, sql, jdbcPara, cols);

            // 如果不支持getGeneratedKeys，就设置自增长值为null
            if (!executeContext.sqlManager.getDbStyle().generatedKeysSupport()) {
                if (holderHasAttr) {
                    int length = holder.getAttrNames().length;
                    Object[] values = new Object[length];
                    for (int i = 0; i < length; i++) {
                        values[i] = null;
                    }

                    holder.setValues(values);
                }
            } else if (holderHasAttr) {
                this.handleHolder(ruh.statement, holder);
            }
            int ret = (Integer) ruh.resultSet;
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
        closeConnection(conn, isUpdate);
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
        executeContext.sqlManager.getDs().closeConnection(conn, this.executeContext, isUpdate);
    }

    protected ResultSetHolder dbQuery(Connection conn, String sql, List<SQLParameter> jdbcPara) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);

        this.applyStatementSetting(executeContext, conn, ps);
        this.setPreparedStatementPara(ps, jdbcPara);
        ResultSet rs = ps.executeQuery();

        return new ResultSetHolder(ps, rs);
    }

    protected ResultUpdateHolder dbUpdate(Connection conn, String sql, List<SQLParameter> jdbcPara)
            throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        this.applyStatementSetting(executeContext, conn, ps);
        this.setPreparedStatementPara(ps, jdbcPara);
        int result = ps.executeUpdate();
        return new ResultUpdateHolder(ps, result);
    }

    protected ResultUpdateHolder dbUpdateWithHolder(Connection conn, String sql, List<SQLParameter> jdbcPara,
                                                    String[] cols) throws SQLException {
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

    protected void clean(ExecuteContext executeContext, Connection conn) {
        this.clean(true, conn, (Closeable) null);
    }

    /**
     * 插入单条数据后，数据库自动生成值赋值给Holder
     *
     * @param ps     Statement
     * @param holder KeyHolder
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed <code>Statement</code>
     */
    protected void handleHolder(Statement ps, KeyHolder holder) throws SQLException {
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            int length = holder.getAttrNames().length;
            Object[] values = new Object[length];
            for (int i = 0; i < length; i++) {
                values[i] = rs.getObject(i + 1);
            }
            holder.setValues(values);
        }
        rs.close();
    }

    /**
     * 把{@code KeyHolder}的值赋值回Bean
     *
     * @param holder
     * @param paras
     */
    protected void assignKeyHolder(KeyHolder holder, Object paras) {
        if (paras instanceof Map) {
            return;
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

        if (!(executeContext.sqlSource instanceof SQLTableSource)) {
            //sqlId
            return;
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
                if (StringKit.isEmpty(algorithm)) {
                    throw new BeetlSQLException(BeetlSQLException.ID_NOT_FOUND, "使用@Assign未指定算法，需要显示的赋值给 " + attrName);
                }
                String param = assignId.param();
                Object o = executeContext.sqlManager
                        .getAssignIdByIdAutoGen(algorithm, param, tableSource.getTableDesc().getName());
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
    public Map beforeExecute(Class target, Object paras, boolean isUpdate) {
        executeContext.target = target;
        executeContext.inputParas = paras;
        executeContext.isUpdate = isUpdate;


        if (paras == null) {
            Map map = new HashMap();
            addMoreParasFromTarget(target, map);
            return map;
        }
        if (paras instanceof Map) {
            Map map = (Map) paras;
            addMoreParasFromTarget(target, map);
            return map;
        }

        Map map = new HashMap();

        map.put("_root", paras);
        Class parasType = paras.getClass();
        if (isBaseDataType(paras.getClass())) {
            return map;
        }


        //处理入参是否需要变化
        ClassAnnotation an = ClassAnnotation.getClassAnnotation(parasType);
        if (an.isContainExtAnnotation()) {
            ClassExtAnnotation ext = an.getExtAnnotation();
            if (ext.hasAttributeExt()) {
                Map<String, AttributeConvert> attrMap = ext.getAttributeConvertMap();
                for (Map.Entry<String, AttributeConvert> entry : attrMap.entrySet()) {
                    String attr = entry.getKey();
                    AttributeConvert convert = entry.getValue();
                    Object newValue = convert.toDb(executeContext, parasType, attr, paras);
                    //相对于"_root"，模板引擎会先找map下的属性
                    map.put(attr, newValue);
                }
            }
            if (ext.hasEntityExt()) {
                BeanConvert beanConvert = ext.getBeanConvert();
                if (beanConvert != null) {
                    //得到新的paras
                    Object obj = beanConvert.before(executeContext, paras, ext.getBeanConvertAnnotation());
                    if (obj instanceof Map) {
                        map.putAll((Map) obj);
                    } else {
                        map.put("_root", obj);
                    }
                }
            }
        }
        addMoreParasFromTarget(target, map);


        return map;
    }

    protected void addMoreParasFromTarget(Class target, Map map) {

        Map extPara = this.executeContext.sqlManager.getSqlManagerExtend().getParaExtend().morePara(this.executeContext);
        if (extPara != null) {
            map.putAll(extPara);
        }

        if (target == null) {
            return;
        }
        ClassAnnotation targetClassAnnotation = ClassAnnotation.getClassAnnotation(target);
        if (targetClassAnnotation.isContainExtAnnotation()) {
            ClassExtAnnotation targetExtClassAnnotation = targetClassAnnotation.getExtAnnotation();
            Annotation annotation = targetExtClassAnnotation.getAdditionalAnnotation();
            if (annotation != null) {
                TargetAdditional targetAdditional = targetExtClassAnnotation.getTargetAdditional();
                Map moreParas = targetAdditional.getAdditional(executeContext, annotation);
                if (moreParas != null && !moreParas.isEmpty()) {
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

        List<SQLParameter> jdbcPara = new LinkedList<>();
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
        result.templateContext = t.getContext();
        return result;
    }

    /**
     * {@link Interceptor} sql执行前置拦截器
     *
     * @param inputParas 执行sql传入的参数
     * @return 如果 {@code SqlManager} 的 {@code Interceptor[]} 长度大于0返回 {@code InterceptorContext}
     * <p>
     * 否则返回 {@code null}
     */
    protected InterceptorContext callInterceptorAsBefore(Map<String, Object> inputParas) {
        Interceptor[] inters = executeContext.sqlManager.inters;
        if (inters.length > 0) {
            InterceptorContext ctx = new InterceptorContext(executeContext);
            for (Interceptor in : inters) {
                in.before(ctx);
            }
            return ctx;
        }
        return null;
    }

    /**
     * {@link Interceptor} sql执行后置拦截器
     *
     * @param ctx    前置拦截器返回的 {@code InterceptorContext} 或 {@code null}
     * @param result sql执行结果
     */
    protected void callInterceptorAsAfter(InterceptorContext ctx, Object result) {
        if (ctx == null) {
            return;
        }
        for (Interceptor in : executeContext.sqlManager.inters) {
            in.after(ctx);
        }
    }

    /**
     * {@link Interceptor} sql执行异常拦截器
     *
     * @param ctx 前置拦截器返回的 {@code InterceptorContext} 或 {@code null}
     * @param ex  sql执行异常信息
     */
    protected void callInterceptorAsException(InterceptorContext ctx, Exception ex) {
        if (ctx == null) {
            return;
        }
        for (Interceptor in : executeContext.sqlManager.inters) {
            in.exception(ctx, ex);
        }
    }

    /**
     * bean后处理
     *
     * @param result
     */
    protected Object afterBean(Object result) {
        if (result == null) {
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
        if (extAnnotation != null && extAnnotation.getBeanConvert() != null) {
            BeanConvert convert = extAnnotation.getBeanConvert();
            if (convert != null) {
                Annotation annotation = extAnnotation.getBeanConvertAnnotation();
                if (result instanceof List) {
                    List resultList = (List) result;
                    resultList.forEach(obj -> convert.after(executeContext, obj, annotation));
                } else {
                    convert.after(executeContext, result, annotation);
                }

            }
        }

        BeanFetch beanFetch = classAnnotation.getBeanFetch();
        if (beanFetch != null) {
            if (result instanceof List) {
                List resultList = (List) result;
                beanFetch.fetchMore(executeContext, resultList, classAnnotation.getBeanFetchAnnotation());
            } else {
                List list = new ArrayList(1);
                list.add(result);
                beanFetch.fetchMore(executeContext, list, classAnnotation.getBeanFetchAnnotation());
                result = list.get(0);
            }

        }

        return result;

    }


    protected void applyStatementSetting(ExecuteContext ctx, Connection conn, Statement statement) throws SQLException {
        this.getExecuteContext().sqlManager.getDs().applyStatementSetting(ctx, conn, statement);
    }

    interface Closeable {
        void close() throws SQLException;
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

	@Data
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

}
