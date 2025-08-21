package org.beetl.sql.mapper.internal;


import com.sun.corba.se.impl.orbutil.ObjectUtility;
import org.beetl.ext.fn.ArrayUtil;
import org.beetl.sql.clazz.kit.ArrayKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.ListUtil;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.mapper.MapperInvoke;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 根据属性字段删除数据
 *
 * @author liuminct@163.com
 */
public class DeleteByPropAmi extends MapperInvoke {

    @Override
    @SuppressWarnings("unchecked")
    public Object call(SQLManager sm, Class entityClass, Method m, Object[] args) {
        LambdaQuery.Property<Object, Object> prop = (LambdaQuery.Property<Object, Object>)args[0];
        Object[] propValues = (Object[])args[1];
        if (prop == null || ArrayKit.isEmpty(propValues)) {
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "属性字段或属性字段值不能为空");
        }
		String tableName = sm.getNc().getTableName(entityClass);
		if (StringKit.isEmpty(tableName)) {
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "根据Class找不到映射的数据库表");
		}
        int rows = 0;
        List<Object> valList = ListUtil.newArrayList(propValues);
		ListUtil.removeDuplicate(valList);
		//使用数据库的批量大小
		int batchSize = sm.getDbStyle().getMaxBatchCount();
        //对属性值分批次，防止IN过长报错
        List<List<Object>> batchVals = ListUtil.partition(valList,batchSize);
        for (List<Object> vals : batchVals) {
            rows += sm.lambdaQuery(entityClass).andIn(prop, vals).delete();
        }
        return rows;
    }
}
