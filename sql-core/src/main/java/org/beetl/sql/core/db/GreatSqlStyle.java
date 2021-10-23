package org.beetl.sql.core.db;

/**
 * 国产开源分布式数据库
 */
public class GreatSqlStyle extends  MySqlStyle{
	public GreatSqlStyle(){
		super();
		this.keyWordHandler = null;
	}

	@Override
	public String getName() {
		return "greatsql";
	}

	@Override
	public int getDBType() {
		return DBType.DB_GREATSQL;
	}




}
