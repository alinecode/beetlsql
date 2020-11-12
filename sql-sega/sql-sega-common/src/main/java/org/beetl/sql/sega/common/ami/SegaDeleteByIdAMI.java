package org.beetl.sql.sega.common.ami;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.sega.common.LocalSegaContext;
import org.beetl.sql.sega.common.SegaContext;
import org.beetl.sql.sega.common.SegaRollbackTask;

import java.lang.reflect.Method;

/**
 *
 * @author xiandafu
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
		SegaContext segaContext = LocalSegaContext.segaContextFactory.current();
		segaContext.getTransaction().addTask(new DeleteSegaRollbackTask(sm.getName(),before));
		return count;

    }

    public static class DeleteSegaRollbackTask  implements  SegaRollbackTask{
		String sqlManagerName;
		Object obj;
    	public DeleteSegaRollbackTask(String sqlManagerName,Object obj){
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
