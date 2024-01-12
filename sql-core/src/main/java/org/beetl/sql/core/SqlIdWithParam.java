package org.beetl.sql.core;


/**
 * 带参数的sqlId
 *
 * @author liumin
 */
public class SqlIdWithParam {

	/**
	 * sqlId标识
	 */
	private final SqlId sqlId;

	/**
	 * sql参数
	 */
	private final Object sqlParam;

	public SqlIdWithParam(SqlId sqlId, Object sqlParam) {
		this.sqlId = sqlId;
		this.sqlParam = sqlParam;
	}


	public static SqlIdWithParam of(SqlId sqlId, Object sqlParam) {
		return new SqlIdWithParam(sqlId, sqlParam);
	}


	public Object getSqlParam() {
		return sqlParam;
	}

	public SqlId getSqlId() {
		return sqlId;
	}
}
