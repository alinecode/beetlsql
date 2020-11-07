package org.beetlsql.sql.sega.test;

import org.beetl.sql.sega.common.SegaMapper;
import org.beetl.sql.sega.common.annotation.SegaUpdateSql;

public interface UserMapper extends SegaMapper<User> {
	@SegaUpdateSql(
		sql="update stock set count=count+1 where id=?",
		rollback = "update stock set count=count-1 where id=?"
	)
	void addStock(String id);
}
