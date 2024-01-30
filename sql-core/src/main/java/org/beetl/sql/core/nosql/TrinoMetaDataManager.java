package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;


public class TrinoMetaDataManager extends ClickhouseMetaDataManager{
	public TrinoMetaDataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
	}

}
