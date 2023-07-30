package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.db.DBStyle;

/**
 * 用于定义SQL语句的加载接口
 */
 public interface SQLLoader {


	 /**
	  * 从缓存中取得SQLSource，抛出错误 BeetlSQLException.CANNOT_GET_SQL
	  *
	  * @param id
	  * @return 返回对应SQL源
	  */
	 SQLSource querySQL(SqlId id);

	/**
	 * 同querySQL，但总是加载新的sql
	 * @param id
	 * @return
	 */
	SQLSource loadSQL(SqlId id);

	 /**
	  * 查询自动生成，或者Java提供的Sql
	  * @param id
	  * @return
	  */
	 SQLSource queryAutoSQL(SqlId id);

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
	 * 检测是否存在sqlId的namespace，如文件
	 * @param id
	 * @return
	 */
	boolean existNamespace(SqlId id);

	 /**
	  * 新增一个指定标识的SQL资源
	  *
	  * @param id 指定SqlId
	  * @param source 新增SQL资源
	  */
	 void addSQL(SqlId id, SQLSource source);


	/**
	 * 新增一个指定标识的SQL资源，为通过POJO生成的内置SQL
	 *
	 * @param id 指定SqlId
	 * @param source 新增SQL资源
	 */
	void addAutoGenSQL(SqlId id, SQLSource source);

	 /**
	  * 获取数据库方言配置
	  * @return DBStyle
	  */
	 DBStyle getDbStyle();

	 void setDbStyle(DBStyle dbs);

	/**
	 * 是否是生产配置
	 */
	 boolean isProduct();

	 void setProduct(boolean product);

	/**
	 * 返回{@link SqlId} 表示的SQL资源路径
	 */
	 String getPathBySqlId(SqlId id);

	 BeetlSQLException getException(SqlId sqlId);

	 void refresh();
 }
