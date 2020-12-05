package org.beetl.sql.saga.ms.server.dao;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Sql;
import org.beetl.sql.saga.ms.server.entity.RollbackTaskEntity;

import java.util.List;

public interface RollbackTaskMapper extends BaseMapper<RollbackTaskEntity> {
	@Sql("select count(1) from rollback_task t where t.gid=? and t.time<time")
	public int findEarlierTransaction(String gid,Long time);
	@Sql("select * from rollback_task t where t.gid=? order by time desc")
	List<RollbackTaskEntity> allRollbackTask(String gid);
}
