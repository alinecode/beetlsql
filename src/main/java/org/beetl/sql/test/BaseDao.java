package org.beetl.sql.test;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.mapper.BaseMapper;

import java.util.List;

public interface BaseDao<T> extends BaseMapper<T> {
    T findOne(@Param("id") Long id);
    List<T> queryAll();
}
