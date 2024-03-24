package org.beetl.sql.core.db;

/**
 * https://ymatrix.cn/doc/4.3/install/mx4_docker
 */
public class YMatrixStyle extends PostgresStyle {
	@Override
	public String getName() {
		return "ymatrix";
	}

	@Override
	public int getDBType() {

		return DBType.DB_YMARTIX;
	}
}
