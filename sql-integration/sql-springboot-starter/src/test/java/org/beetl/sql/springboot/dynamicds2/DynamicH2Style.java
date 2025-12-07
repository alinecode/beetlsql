package org.beetl.sql.springboot.dynamicds2;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.core.meta.MetadataManager;

public class DynamicH2Style extends H2Style {
	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs) {
		metadataManager = new DynamicSourceSchemaMetadataManager(cs, this);
		return metadataManager;
	}

	@Override
	public MetadataManager initMetadataManager(ConnectionSource cs, String defaultSchema, String defalutCatalog) {
		metadataManager = new DynamicSourceSchemaMetadataManager(cs, defaultSchema, defalutCatalog, this);
		return metadataManager;
	}
}
