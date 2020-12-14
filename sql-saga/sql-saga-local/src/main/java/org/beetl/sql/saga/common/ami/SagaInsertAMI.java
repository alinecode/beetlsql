package org.beetl.sql.saga.common.ami;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.saga.common.SagaContext;
import org.beetl.sql.saga.common.SagaRollbackTask;

import java.lang.reflect.Method;

public class SagaInsertAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		int ret = sm.insert(args[0]);
		SagaContext sagaContext = SagaContext.sagaContextFactory.current();
		Class target = args[0].getClass();
		String idAttr = sm.getClassDesc(target).getIdAttr();
		Object key = BeanKit.getBeanProperty(args[0], idAttr);
		sagaContext.getTransaction().addTask(new InsertSagaRollbackTask(sm.getName(), target, key));
		return ret;
	}

	@Data
	public static class InsertSagaRollbackTask implements SagaRollbackTask {
		String sqlManagerName;
		Class entityClass;
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@Clazz")
		Object pkId;

		public InsertSagaRollbackTask() {
			//反序列化用
		}

		public InsertSagaRollbackTask(String sqlManagerName, Class entityClass, Object pkId) {
			this.sqlManagerName = sqlManagerName;
			this.entityClass = entityClass;
			this.pkId = pkId;
		}

		@Override
		public boolean call() {
			SQLManager sqlManager = SQLManagerBuilder.sqlManagerMap.get(sqlManagerName);
			int count = sqlManager.deleteById(entityClass, pkId);
			if (count != 1) {
				//数据库没数据，可能是主从切换，数据还在路上
				return false;
			}
			return true;
		}
	}
}
