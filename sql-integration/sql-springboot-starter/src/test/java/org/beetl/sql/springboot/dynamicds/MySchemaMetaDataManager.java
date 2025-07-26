package org.beetl.sql.springboot.dynamicds;

import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.meta.SchemaMetadataManager;

import java.util.function.Consumer;

/**
 * 多租户（数据源）情况下，表信息schema和catalog设置为null，具体需要依赖多租户如何使用多个数据源
 */
public class MySchemaMetaDataManager extends SchemaMetadataManager {
	public MySchemaMetaDataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
	}

	public MySchemaMetaDataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
		super(ds,defaultSchema,defaultCatalog,style);

	}
	public void refresh() {
		super.refresh();
		this.tableInfoMap.values().stream().forEach(new Consumer() {
			@Override
			public void accept(Object o) {
				TableDesc tableDesc = (TableDesc)o;
				//设置为null，否则h2数据库加载不到，适合多租户是多数据源情况，因为多个租户共用了这个Schemaanager
				tableDesc.setSchema(null);
				tableDesc.setCatalog(null);
			}
		});
	}
}
