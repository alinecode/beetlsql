package org.beetl.sql.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.util.BatchExecuteUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * create time : 2017-04-27 16:09
 *
 * @author luoyizhu@gmail.com
 */
public class InsertBatchAMI extends MapperInvoke {

    @Override
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		List<Object> list = (List<Object>)args[0];
		int defaultBatchSize = sm.getDbStyle().getMaxBatchCount();
		if(args.length==1&&list.size()<defaultBatchSize){
			//大多数情况
			return sm.insertBatch(entityClass,list);
		}

		int size = args.length==1?defaultBatchSize: ((Number)args[1]).intValue();
		int[] ret = BatchExecuteUtil.executeBatchList(list, size, subList -> {
			int[] subRet =  sm.insertBatch(entityClass, subList);
			return subRet;
		});
		return ret;

    }

}
