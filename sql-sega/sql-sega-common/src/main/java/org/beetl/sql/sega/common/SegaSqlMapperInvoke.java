package org.beetl.sql.sega.common;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.builder.MethodParamsHolder;
import org.beetl.sql.sega.common.annotation.SegaUpdateSql;

import java.lang.reflect.Method;

/**
 *
 * @see SegaUpdateSql
 */
public class SegaSqlMapperInvoke extends MapperInvoke {
	String  updateSql = null;
	String rollbackSql = null;
	MethodParamsHolder holder;
	public SegaSqlMapperInvoke(String updateSql,String rollbackSql){
		this.updateSql =updateSql;
		this.rollbackSql = rollbackSql;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		int ret = sm.executeUpdate(new SQLReady(updateSql,args));
		if(ret==0){
			return ret;
		}
		SegaContext segaContext = LocalSegaContext.segaContextFactory.current();
		segaContext.getTransaction().addTask(new UpdateSqlSegaRollbackTask(sm.getName(),rollbackSql,updateSql,args) );
		return ret;
	}


	public static class UpdateSqlSegaRollbackTask  implements  SegaRollbackTask{
		String sqlManagerName;
		String rollbackSql;
		String updateSql;
		Object[] args;

		public UpdateSqlSegaRollbackTask(String sqlManagerName,String rollbackSql,
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
