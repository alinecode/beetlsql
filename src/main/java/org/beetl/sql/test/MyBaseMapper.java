package org.beetl.sql.test;

import java.util.List;
import java.util.Map;

import org.beetl.sql.core.mapper.BaseMapper;

public interface MyBaseMapper<T>  {
	public T single(Object key);
	public T single(Object key,List attrs);
	public List<T> allData();
}
