package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.db.DBStyle;

public interface SQLLoader {


	public void setDbStyle(DBStyle dbs);
	/**
	 * 从缓存中取得SQLSource，抛出错误 BeetlSQLException.CANNOT_GET_SQL
	 * @param id
	 * @return
	 */
	public SQLSource querySQL(SqlId id);


	/**
	 * 判断一个sql是否修改过
	 * @param id
	 * @return
	 */
	public boolean isModified(SqlId id);
	/**
	 * 判断一个sql是否存在
	 * @param id
	 * @return
	 */
	public boolean exist(SqlId id);
	/**
	 * SQLLoader里增加一个预先有的sql，如自动生成的SQL
	 * @param id
	 * @param source
	 */
	public void addSQL(SqlId id, SQLSource source);


	public DBStyle getDbStyle() ;

	public boolean isProduct();

	public void setProduct(boolean product);


	public String getPathBySqlId(SqlId id);

	public BeetlSQLException getException(SqlId id);


	
}
