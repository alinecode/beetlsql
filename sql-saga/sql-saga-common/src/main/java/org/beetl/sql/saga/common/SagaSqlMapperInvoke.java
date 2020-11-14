package org.beetl.sql.saga.common;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.saga.common.annotation.SegaUpdateSql;

import java.lang.reflect.Method;

/**
 *
 * @see SegaUpdateSql
 */
public class SagaSqlMapperInvoke extends MapperInvoke {
	String  updateSql = null;
	String rollbackSql = null;
	MethodParamsHolder holder;
	public SagaSqlMapperInvoke(String updateSql,String rollbackSql){
		this.updateSql =updateSql;
		this.rollbackSql = rollbackSql;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		int ret = sm.executeUpdate(new SQLReady(updateSql,args));
		if(ret==0){
			return ret;
		}
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		sagaContext.getTransaction().addTask(new UpdateSqlSagaRollbackTask(sm.getName(),rollbackSql,updateSql,args) );
		return ret;
	}


	public static class UpdateSqlSagaRollbackTask implements SagaRollbackTask {
		String sqlManagerName;
		String rollbackSql;
		String updateSql;
		Object[] args;

		public UpdateSqlSagaRollbackTask(String sqlManagerName,String rollbackSql,
				String updateSql,
				Object[] args){
			this.sqlManagerName = sqlManagerName;
			this.rollbackSql = rollbackSql;
			this.updateSql = updateSql;
			this.args = args;
		}

		@Override
		public boolean call() {
			SQLManager sqlManager = SQLManagerBuilder.sqlManagerMap.get(sqlManagerName);
			int ret = sqlManager.executeUpdate(new SQLReady(rollbackSql,args));
			if(ret==0){
				return false;
			}
			return true;
		}
	}
}
