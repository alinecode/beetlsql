package org.beetl.sql.sega.common.ami;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaRollbackTask;

import java.lang.reflect.Method;

public class SegaInsertAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		int ret = sm.insert(args[0]);
		SegaContext segaContext = SegaContext.segaContextFactory.current();
		segaContext.getTransaction().addTask(new SegaRollbackTask() {
			@Override
			public boolean call() {
				Class target = args[0].getClass();
				String idAttr = sm.getClassDesc(target).getIdAttr();
				Object key = BeanKit.getBeanProperty(args[0],idAttr);
				int count = sm.deleteById(args[0].getClass(),key);
				if(count!=1){
					//数据库没数据，可能是主从切换，数据还在路上
					return false;
				}
				return true;
			}
		});
		return ret;
	}
}
