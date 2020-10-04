package org.beetl.sql.core.db;

public class OpenGaussStyle  extends PostgresStyle{
    @Override
    public String getName() {
        return "opengauss";
    }

    @Override
    public int getDBType() {
        return DBType.DB_HUAWEI_OPENGAUSS;
    }

}
