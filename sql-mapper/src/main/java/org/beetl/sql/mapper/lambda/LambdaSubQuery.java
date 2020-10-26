package org.beetl.sql.mapper.lambda;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.query.LambdaQuery;

import java.util.Arrays;

/**
 * 增强LambdaQuery查询，允许单表是一个子查询，子查询来源于sqlId
 * @see "https://gitee.com/xiandafu/beetlsql/issues/I1WRHZ"
 * @author xiandafu
 */
public class LambdaSubQuery<T> extends LambdaQuery<T> {
	private SqlId sqlId;
	private Object paras;
	public LambdaSubQuery(SQLManager sqlManager, Class<?> clazz, SqlId sqlId) {
		this(sqlManager, clazz, sqlId, null);
	}

	public LambdaSubQuery(SQLManager sqlManager, Class<?> clazz, SqlId sqlId, Object paras) {
		super(sqlManager, clazz);
		this.sqlId = sqlId;
		this.paras = paras;
	}

	@Override
	public String getTableName(Class<?> c) {
		SQLResult sqlResult = this.sqlManager.getSQLResult(sqlId, paras);
		// 将sqlId的查询参数增加在前面
		getParams().addAll(0, Arrays.asList(sqlResult.toObjectArray()));
		//拼接sql： ( sql ) t
		return new StringBuilder("( ").append(sqlResult.jdbcSql).append(" ) _t").toString();
	}
}
