package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

@SqlResource("wan.?")
public interface BaseDao< T extends BaseInfo> extends BaseMapper<BaseInfo>{
	void getIds3(PageQuery<?> query);
}
