package org.beetl.sql.trino;

import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;

@SqlResource("system.order")
public interface TrinoOrderMapper  extends BaseMapper<TrinoOrder> {
	public PageResult<TrinoOrder> select(PageRequest pageRequest);
}
