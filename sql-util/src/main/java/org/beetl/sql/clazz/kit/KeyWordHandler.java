package org.beetl.sql.clazz.kit;

/**
 * 不同数据库的关键字在SQL中的字面量使用差异处理<br/>
 * 例如在mysql中使用type作为列别名时，需要用反单引号 <b>`</b> 包围 <b>`type`</b>
 *
 * @author xiandafu
 */
public interface KeyWordHandler {
	/**
	 * 返回差异处理后的表名
	 */
	String getTable(String tableName);

	/**
	 * 返回差异处理后的列名
	 */
	String getCol(String colName);
}
