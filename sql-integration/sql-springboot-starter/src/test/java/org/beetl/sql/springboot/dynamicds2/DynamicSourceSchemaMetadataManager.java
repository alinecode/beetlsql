package org.beetl.sql.springboot.dynamicds2;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.meta.SchemaMetadataManager;

public class DynamicSourceSchemaMetadataManager extends SchemaMetadataManager {
	public DynamicSourceSchemaMetadataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
		this.defaultSchema = null;
		this.defaultCatalog=null;
	}

	public DynamicSourceSchemaMetadataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
		super(ds, defaultSchema, defaultCatalog, style);
		this.defaultSchema = null;
		this.defaultCatalog=null;
	}
}
