package org.beetl.sql.sega.common.ami;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaRollbackTask;

import java.lang.reflect.Method;

/**
 * create time : 2017-04-27 16:07
 *
 * @author luoyizhu@gmail.com
 */
public class SegaDeleteByIdAMI extends MapperInvoke {

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

		SegaContext segaContext = SegaContext.segaContextFactory.current();
		segaContext.getTransaction().addTask(new SegaRollbackTask() {
			@Override
			public boolean call() {
				sm.insert(before);
				return true;
			}
		});

		return count;

    }

}
