package org.beetl.sql.core.db;

/**
 * 阿里云数据库 polar，这里使用了兼容postgres
 * @author xiandafu
 *
 */
public class PolarDBStyle extends PostgresStyle{
    @Override
    public String getName() {
        return "polardb";
    }

    @Override
    public int getDBType() {
        return DBType.DB_ALIYUN_POLARDB;
    }

}
