package org.beetl.sql.clazz.kit;

/**
 * 表名字和列名是否需要加特殊符号，比如有些表,user表,需要变成`user`
 * @author xiandafu
 */
public interface KeyWordHandler {
    String getTable(String tableName);
    String getCol(String colName);
}
