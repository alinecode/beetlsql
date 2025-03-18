package org.beetl.sql.core.meta;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;

/**
 * 用于多租户场景，特别是多库多租户或者多schema多租户情况
 * @see "https://gitee.com/xiandafu/springboot3-beetl-beetlsql-example" 多库多租户
 */
public class IgnoreSchemaMetadataManager  extends SchemaMetadataManager{
	public IgnoreSchemaMetadataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
		setNull();
	}

	public IgnoreSchemaMetadataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
		super(ds, defaultSchema, defaultCatalog, style);
		setNull();
	}

	/**
	 * beetlsq会根据jdbc connection（比如oracle）加载一次数据库的所有表名字，并在后续调用懒加载表的详细metadata
	 * 在多租户场景下，延迟加载会出现问题，当另外一个jdbc connection（比如mysql） 再使用SchemaMetadataManager存放的catalog（此时是最初的Oracle的）
	 * 加载表详细metadata 会出现加载不到情况。
	 *
	 * 因此 IgnoreSchemaMetadataManager将在第一次初始化成功后，吧schema和catalog都设置为null，避免后续懒加载其他库会报错
	 *
	 *
	 */
	public void setNull(){
		this.defaultSchema = null;
		this.defaultCatalog=null;
	}
}
