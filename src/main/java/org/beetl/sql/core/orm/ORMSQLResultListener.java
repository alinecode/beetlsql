package org.beetl.sql.core.orm;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;
import org.beetl.sql.core.SQLResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ORMSQLResultListener implements SQLResultListener {

	List<MappingEntity> mapingEntrys = new ArrayList<MappingEntity>(3);


	public List<MappingEntity> getMapingEntrys() {
		return mapingEntrys;
	}

	public void setMapingEntrys(List<MappingEntity> mapingEntrys) {
		this.mapingEntrys = mapingEntrys;
	}

	@Override
	public List dataSelectd(List list, Map<String, Object> paras, SQLManager sqlManager, SQLResult sqlResult) {
		for (MappingEntity mapConfig : mapingEntrys) {
			mapConfig.map(list, sqlManager, paras);
		}
		return list;
	}
}
