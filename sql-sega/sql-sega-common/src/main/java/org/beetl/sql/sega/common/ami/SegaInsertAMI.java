package org.beetl.sql.sega.common.ami;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.sega.common.LocalSegaContext;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaRollbackTask;

import java.lang.reflect.Method;

public class SegaInsertAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		int ret = sm.insert(args[0]);
		SegaContext segaContext = LocalSegaContext.segaContextFactory.current();
		Class target = args[0].getClass();
		String idAttr = sm.getClassDesc(target).getIdAttr();
		Object key = BeanKit.getBeanProperty(args[0],idAttr);
		segaContext.getTransaction().addTask(new InsertSegaRollbackTask(sm.getName(),target,key) );
		return ret;
	}

	public static class InsertSegaRollbackTask  implements  SegaRollbackTask{
		String sqlManagerName;
		Class entityClass;
		Object pkId;

		public InsertSegaRollbackTask(String sqlManagerName,Class entityClass,Object pkId){
			this.sqlManagerName = sqlManagerName;
			this.entityClass = entityClass;
			this.pkId = pkId;
		}

		@Override
		public boolean call() {
			SQLManager sqlManager = SQLManagerBuilder.sqlManagerMap.get(sqlManagerName);
			int count = sqlManager.deleteById(entityClass,pkId);
			if(count!=1){
				//数据库没数据，可能是主从切换，数据还在路上
				return false;
			}
			return true;
		}
	}
}
