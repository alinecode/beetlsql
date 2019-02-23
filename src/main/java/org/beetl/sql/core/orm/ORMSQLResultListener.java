package org.beetl.sql.core.orm;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResultListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ORMSQLResultListener implements SQLResultListener {

    List<MappingEntity> mapingEntrys = new ArrayList<MappingEntity>(3);

    @Override
    public List<Object> dataSelectd(List<Object> list, Map<String,Object> paras, SQLManager sqlManager, String sqlId, String sql) {
        return null;
    }

    public List<MappingEntity> getMapingEntrys() {
        return mapingEntrys;
    }

    public void setMapingEntrys(List<MappingEntity> mapingEntrys) {
        this.mapingEntrys = mapingEntrys;
    }
}
