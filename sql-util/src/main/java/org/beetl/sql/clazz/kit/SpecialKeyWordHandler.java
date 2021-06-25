package org.beetl.sql.clazz.kit;

public class SpecialKeyWordHandler implements KeyWordHandler{
    @Override
    public String getTable(String tableName) {
        return "`"+tableName+"`";
    }

    @Override
    public String getCol(String colName) {
        return "`"+colName+"`";
    }
}
