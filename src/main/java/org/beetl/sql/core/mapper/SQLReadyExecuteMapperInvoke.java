package org.beetl.sql.core.mapper;

import java.lang.reflect.Method;
import java.util.List;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.PageKit;

/**
 * 执行jdbc sql
 *
 * @author xiandafu, luoyizhu
 */
public class SQLReadyExecuteMapperInvoke extends BaseMapperInvoke {
	int type;

	public SQLReadyExecuteMapperInvoke(int type) {
		this.type = type;
	}

	@Override
	public Object call(SQLManager sm, Class entityClass, String sqlId, Method m, Object[] args) {
		if (type == MethodDesc.SM_SELECT_SINGLE || type == MethodDesc.SM_SELECT_LIST) {
			MethodDesc desc = MethodDesc.getMetodDesc(sm, entityClass, m, sqlId);
			Class returnType = desc.renturnType;
			List list = sm.execute(new SQLReady(sqlId, args), returnType);
			if (type == MethodDesc.SM_SELECT_SINGLE) {
				return list.size() == 0 ? null : list.get(0);
			} else {

				return list;
			}
		}

		// 分页对象
		if (type == MethodDesc.SM_SQL_READY_PAGE_QUERY) {
			return this.getPage(sm, sqlId, entityClass, args, m);
		}

		return sm.executeUpdate(new SQLReady(sqlId, args));

	}

	/**
	 * 分页
	 * 
	 * <pre>
	 * 通过注解sql实现的分页功能, 必须传入两个参数
	 * 参数1: pageNumber
	 * 参数2: pageSize
	 * 顺序不能乱, 变量名没要求.
	 * </pre>
	 *
	 * @param sm
	 *            SQLManager
	 * @param sql
	 *            正常的sql查询语句
	 * @param clazz
	 *            需要转换的类型
	 * @param args
	 *            查询条件
	 * @param <T>
	 *            T
	 * @return 分页对象
	 */
	protected <T> PageQuery<T> getPage(SQLManager sm, String sql, Class<T> clazz, Object[] args, Method m) {

		// 分页去除前面两个参数 (页码, 每页显示多少)
		Object[] conditionArgs = null;
		if (args.length > 2) {
			conditionArgs = new Object[args.length - 2];
			for (int i = 0; i < conditionArgs.length; i++) {
				conditionArgs[i] = args[i + 2];
			}
		}
		long pageNumber = 0;
		long pageSize = 0;
		if (args[0] instanceof Number && args[1] instanceof Number) {
			pageNumber = ((Number) args[0]).longValue();
			pageSize = ((Number) args[1]).longValue();
		} else {
			throw new BeetlSQLException(BeetlSQLException.ERROR_MAPPER_PARAMEER,
					"PageQuery查询期望前俩个参数是Number类型，分别是pageNumber和pageSize:" + m);
		}

		PageQuery pageQuery = new PageQuery(pageNumber, pageSize);
//		System.out.println(sql);
		sm.execute(new SQLReady(sql, conditionArgs), clazz, pageQuery);

		return pageQuery;
	}

}
