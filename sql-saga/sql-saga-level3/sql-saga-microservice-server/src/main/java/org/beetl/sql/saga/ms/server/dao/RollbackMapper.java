package org.beetl.sql.saga.ms.server.dao;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.mapper.annotation.Update;
import org.beetl.sql.saga.ms.server.entity.RollbackEntity;
import org.beetl.sql.saga.ms.server.entity.RollbackTaskEntity;

import java.util.List;

public interface RollbackMapper extends BaseMapper<RollbackEntity> {
	@Sql("update rollback set success=success+1 where gid=?")
	@Update
	public void addSuccess(String gid);



}
