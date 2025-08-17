package org.beetl.sql.mapper.internal;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.mapper.MapperInvoke;
import org.beetl.sql.mapper.util.BatchExecuteUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 批量插入
 *
 * 为空的属性不插入
 *
 * @author liuminct@163.com
 */
public class InsertTemplateBatchAMI extends MapperInvoke {

	@Override
	public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
		List<Object> entityList = (List<Object>) args[0];
		//使用数据库的批量大小
		int batchSize = sm.getDbStyle().getMaxBatchCount();
		//指定了批量大小，则按照指定大小进行分批
		if (args.length > 1 && args[1] != null) {
			batchSize = (int) args[1];
		}
		return BatchExecuteUtil.executeBatchList(entityList, batchSize,
				subList -> sm.insertBatch(entityClass, subList));
	}
}
