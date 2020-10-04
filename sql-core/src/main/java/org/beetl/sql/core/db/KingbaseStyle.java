package org.beetl.sql.core.db;

public class KingbaseStyle extends MySqlStyle{
    @Override
    public String getName() {
        return "kingbase";
    }

    @Override
    public int getDBType() {
        return DBType.DB_KINGBASE;
    }

}
