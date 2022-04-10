package org.beetl.sql.core.meta;

import org.beetl.sql.clazz.TableDesc;

import java.util.Set;

/**
 * 描述数据库表，视图的元数据信息
 * @see java.sql.DatabaseMetaData
 */
public interface MetadataManager {
	/**
	 * 是否存在某表
	 */
	boolean existTable(String tableName);

	/**
	 * 获取对应表描述信息
	 */
	TableDesc getTable(String name);

	/**
	 * 所有表的名称集合
	 */
	Set<String> allTable();

	/**
	 * 增加一个虚拟表对应
	 */
	void addTableVirtual(String realTable, String virtual);

	/**
	 * 重新加载数据库定义
	 */
	 default  void refresh(){

	 }

}

