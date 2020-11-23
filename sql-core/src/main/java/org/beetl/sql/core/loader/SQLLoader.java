package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.db.DBStyle;

 public interface SQLLoader {


	 /**
	  * 从缓存中取得SQLSource，抛出错误 BeetlSQLException.CANNOT_GET_SQL
	  *
	  * @param id
	  * @return
	  */
	 SQLSource querySQL(SqlId id);

	 /**
	  * 判断一个sql是否修改过
	  *
	  * @param id
	  * @return
	  */
	 boolean isModified(SqlId id);

	 /**
	  * 判断一个sql是否存在
	  *
	  * @param id
	  * @return
	  */
	 boolean exist(SqlId id);

	 /**
	  * SQLLoader里增加一个预先有的sql，如自动生成的SQL
	  *
	  * @param id
	  * @param source
	  */
	 void addSQL(SqlId id, SQLSource source);

	 /**
	  * 获取数据库方言配置
	  * @return DBStyle
	  */
	 DBStyle getDbStyle();

	 void setDbStyle(DBStyle dbs);

	 boolean isProduct();

	 void setProduct(boolean product);


	 String getPathBySqlId(SqlId id);

	 BeetlSQLException getException(SqlId id);
 }
