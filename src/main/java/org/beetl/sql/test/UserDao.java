package org.beetl.sql.test;


import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.Sql;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.annotatoin.SqlStatementType;
import org.beetl.sql.core.mapper.BaseMapper;


public interface UserDao extends BaseMapper<User> {
	
	List<Long> getIds();
	@Sql(value="select id from user")
	List<Long> getIds2();
	@SqlStatement(type=SqlStatementType.SELECT)
	List<Map> getIds3();
	
	List getUsers();
	
}
