package org.beetl.sql.core.db;

public interface KeyWordHandler {
    String getTable(String tableName);

    String getCol(String colName);
}
