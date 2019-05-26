package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.List;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.para.PageQueryParamter;

/**
 * 执行jdbc sql
 *
 * @author xiandafu, luoyizhu
 */
public class SQLReadyExecuteMapperInvoke implements MapperInvoke {
	
	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		MethodDesc desc = MethodDesc.getMetodDescBySqlId(sm, entityClass, m, sqlId);
		int type = desc.type;
		if (type == MethodDesc.SM_SELECT_SINGLE || type == MethodDesc.SM_SELECT_LIST) {
			Class returnType = desc.resultType;
			List list = sm.execute(new SQLReady(sqlId, args), returnType);
			if (type == MethodDesc.SM_SELECT_SINGLE) {
				return list.size() == 0 ? null : list.get(0);
			} else {
				return list;
			}
		}else if (type == MethodDesc.SM_SQL_READY_PAGE_QUERY) {
			// 分页对象
			PageQueryParamter parameter = (PageQueryParamter)desc.parameter;
			PageQuery pageQuery = (PageQuery)parameter.get(args);
			Object[] jdcbArgs = parameter.getJdbcArgs(args);
			sm.execute(new SQLReady(sqlId,jdcbArgs), entityClass, pageQuery);
			return pageQuery;
		}else{
			return sm.executeUpdate(new SQLReady(sqlId, args));
		}

		

	}

	

}
