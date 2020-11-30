package org.beetl.sql.saga.common.ami;

import lombok.Data;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaRollbackTask;

import java.lang.reflect.Method;

/**
 *
 * @author xiandafu
 */
public class SagaDeleteByIdAMI extends MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        Object before = sm.single(entityClass,args[0]);
        if(before==null){
        	return 0;
		}
        int count = sm.deleteById(entityClass, args[0]);
        if(count==0){
        	return 0;
		}
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		sagaContext.getTransaction().addTask(new DeleteSagaRollbackTask(sm.getName(),before));
		return count;

    }
	@Data
    public static class DeleteSagaRollbackTask implements SagaRollbackTask {
		String sqlManagerName;
		Object obj;

		public DeleteSagaRollbackTask(){
			//反序列化用
		}
    	public DeleteSagaRollbackTask(String sqlManagerName,Object obj){
			this.sqlManagerName = sqlManagerName;
			this.obj = obj;
		}

		@Override
		public boolean call() {
			SQLManager sqlManager = SQLManagerBuilder.sqlManagerMap.get(sqlManagerName);
			sqlManager.insert(obj);
			return true;
		}
	}

}
