package org.beetl.sql.core;

import java.util.List;
import java.util.Map;

public interface SQLResultListener {
    public List<Object>  dataSelectd(List list, Map<String,Object> paras , SQLManager sqlManager, String sqlId, String sql);
}
