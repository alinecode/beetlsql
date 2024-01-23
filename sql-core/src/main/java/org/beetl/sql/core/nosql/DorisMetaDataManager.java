package org.beetl.sql.core.nosql;

import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;

public class DorisMetaDataManager extends ClickhouseMetaDataManager{
	public DorisMetaDataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
	}


}
