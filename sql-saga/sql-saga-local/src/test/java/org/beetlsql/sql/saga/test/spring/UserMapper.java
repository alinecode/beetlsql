package org.beetlsql.sql.saga.test.spring;

import org.beetl.sql.saga.common.SagaMapper;
import org.beetl.sql.saga.common.annotation.SagaUpdateSql;

public interface UserMapper extends SagaMapper<User> {
	@SagaUpdateSql(
		sql="update stock set count=count+1 where id=?",
		rollback = "update stock set count=count-1 where id=? and count!=0"
	)
	void addStock(String id);
}
