package org.beetl.sql.ext.spring4.dao;

import java.util.List;

import org.beetl.sql.core.mapper.BaseMapper;
import org.beetl.sql.ext.spring4.UserEntity;

/**
 * 定义一个演示用的Mapper
 */
public interface DemoDao extends BaseMapper<UserEntity>{
	List<UserEntity> queryAll();
}
