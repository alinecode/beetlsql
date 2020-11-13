package org.beetlsql.sql.saga.test;

import org.beetl.sql.saga.common.SagaMapper;
import org.beetl.sql.saga.common.annotation.SegaUpdateSql;

public interface UserMapper extends SagaMapper<User> {
	@SegaUpdateSql(
		sql="update stock set count=count+1 where id=?",
		rollback = "update stock set count=count-1 where id=?"
	)
	void addStock(String id);
}
