package org.beetl.sql.test;

import org.apache.poi.hssf.record.formula.functions.T;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.mapper.BaseMapper;

public interface BaseDao<T> extends BaseMapper<T> {
	public void selectPage(PageQuery<T> query);
}
