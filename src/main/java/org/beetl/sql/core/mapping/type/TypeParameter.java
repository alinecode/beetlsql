package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TypeParameter {
    String dbName;
    Class target;
    ResultSet rs;
    ResultSetMetaData meta;
    int index;
    String sqlId;

    public TypeParameter(String sqlId, String dbName, Class target, ResultSet rs, ResultSetMetaData meta, int index) {
        super();
        this.dbName = dbName;
        this.target = target;
        this.rs = rs;
        this.meta = meta;
        this.index = index;
        this.sqlId = sqlId;
    }

    public boolean isPrimitive() {
        return target != null ? target.isPrimitive() : false;
    }

    public int getColumnType() throws SQLException {
        return meta.getColumnType(index);
    }

    public Object getObject() throws SQLException {
        return rs.getObject(index);
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Class getTarget() {
        return target;
    }

    public void setTarget(Class target) {
        this.target = target;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public ResultSetMetaData getMeta() {
        return meta;
    }

    public void setMeta(ResultSetMetaData meta) {
        this.meta = meta;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }


}
