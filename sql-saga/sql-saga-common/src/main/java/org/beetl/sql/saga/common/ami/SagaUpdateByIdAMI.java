package org.beetl.sql.saga.common.ami;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaRollbackTask;
import org.beetl.sql.saga.common.LocalSagaContext;

import java.lang.reflect.Method;

public class SagaUpdateByIdAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		Object obj = args[0];
		Class target = obj.getClass();
		String idAttr = sm.getClassDesc(target).getIdAttr();
		Object key = BeanKit.getBeanProperty(obj,idAttr);
		Object before = sm.single(target,key);
		if(before==null){
			return 0;
		}
		int count = sm.updateById(args[0]);
		if(count==0){
			return count;
		}

		SagaContext sagaContext = LocalSagaContext.sagaContextFactory.current();
		sagaContext.getTransaction().addTask(new UpdateSagaRollbackTask(sm.getName(),before) );

		return count;

	}

	public static class UpdateSagaRollbackTask implements SagaRollbackTask {
		String sqlManagerName;
		Object obj;
		public UpdateSagaRollbackTask(String sqlManagerName,Object obj){
			this.sqlManagerName = sqlManagerName;
			this.obj = obj;
		}

		@Override
		public boolean call() {
			SQLManager sm = getSQLManager(sqlManagerName);
			int count = sm.updateById(obj);
			if(count==0){
				return false;
			}
			return true;
		}
	}

}
