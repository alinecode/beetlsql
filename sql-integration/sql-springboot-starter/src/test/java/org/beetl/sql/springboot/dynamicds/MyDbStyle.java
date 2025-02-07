package org.beetl.sql.springboot.dynamicds;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.meta.MetadataManager;

/**
 * 重要，需要设置MySchemaMetaDataManager，此类保证从缓存中能加载表结构
 */
public class MyDbStyle extends H2Style {
	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs) {
		metadataManager = new MySchemaMetaDataManager(cs, this);
		return metadataManager;
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs, String defaultSchema, String defalutCatalog) {
		metadataManager = new MySchemaMetaDataManager(cs, defaultSchema, defalutCatalog, this);
		return metadataManager;
	}
}
