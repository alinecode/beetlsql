package org.beetl.sql.core.db;

public class OceanBaseMyStyle extends MySqlStyle{
	@Override
	public String getName() {
		return "oceanbase";
	}

	@Override
	public int getDBType() {
		return DBType.DB_OCEANBASE;
	}
}
