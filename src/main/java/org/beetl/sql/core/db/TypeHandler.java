package org.beetl.sql.core.db;

public interface TypeHandler {
    Object getDbValue(Object pojoValue);

    Object getPojoeValue(Object dbValue);
}
