package org.beetl.sql.core.meta;

import org.beetl.sql.clazz.TableDesc;

import java.util.Set;

/**
 * 描述数据库表，视图信息
 */
public interface MetadataManager {
     boolean existTable(String tableName);

     TableDesc getTable(String name);

     Set<String> allTable();

     void addTableVirtual(String realTable, String virtual);

}

