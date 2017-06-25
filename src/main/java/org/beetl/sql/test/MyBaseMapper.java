package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.mapper.BaseMapper;

public interface MyBaseMapper<T> extends BaseMapper<T> {
	public List<T> select(Map paras);
}
