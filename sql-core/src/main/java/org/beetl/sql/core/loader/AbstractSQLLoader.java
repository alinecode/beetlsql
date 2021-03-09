package org.beetl.sql.core.loader;

import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.db.DBStyle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 包含了所有的SQL，包含内置和外部。外部sql应该支持根据不同数据库加载不同数据
 */
public abstract class AbstractSQLLoader implements SQLLoader {


	protected DBStyle dbs = null;

	protected boolean product;
	/**
	 * 存放自动生成，或者用户提供的sql
	 */
	protected Map<SqlId, SQLSource> autoGenSourceMap = new ConcurrentHashMap<SqlId, SQLSource>();


	public abstract SQLSource queryExternalSource(SqlId id);

	public abstract boolean existExternalSource(SqlId id);

	public abstract boolean isExternalSourceModified(SqlId id);

	public abstract void removeExternalSource(SqlId id);


	public AbstractSQLLoader() {

	}

	/**
	 * sqlId不存在，抛出一个具体错误异常
	 * @param id
	 * @return
	 */
	@Override
	public SQLSource querySQL(SqlId id) {
		SQLSource sqlSource = autoGenSourceMap.get(id);
		if (sqlSource != null) {
			return sqlSource;
		}
		sqlSource =  queryExternalSource(id);
		return sqlSource;
	}

	@Override
	public SQLSource queryAutoSQL(SqlId id){
		SQLSource sqlSource = autoGenSourceMap.get(id);
		return sqlSource;
	}

	@Override
	public boolean isModified(SqlId id) {
		if (isProduct()) {
			return false;
		}
		if (autoGenSourceMap.containsKey(id)) {
			return false;
		}
		boolean isModified = isExternalSourceModified(id);
		if (isModified) {
			removeExternalSource(id);
		}
		return isModified;

	}

	@Override
	public boolean exist(SqlId id) {
		if (autoGenSourceMap.containsKey(id)) {
			return true;
		}

		return existExternalSource(id);

	}

	@Override
	public DBStyle getDbStyle() {
		return dbs;
	}

	@Override
	public void setDbStyle(DBStyle dbs) {
		this.dbs = dbs;
	}

	@Override
	public void addSQL(SqlId id, SQLSource source) {
		autoGenSourceMap.put(id, source);
	}

	@Override
	public boolean isProduct() {
		return product;
	}

	@Override
	public void setProduct(boolean product) {
		this.product = product;
	}

	@Override
	public String getPathBySqlId(SqlId id) {
		return id.getNamespace().replace('.', '/');
	}


}
