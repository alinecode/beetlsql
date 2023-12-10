package org.beetl.sql.mysql;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Call;
import org.beetl.sql.mapper.annotation.CallOutBean;
import org.beetl.sql.mapper.annotation.Update;

public interface MyTestMapper extends BaseMapper {
	@Call("call test.logcount(?,?)")
	@Update
	void logcount(int id, @CallOutBean OutHolder outHolder);
}
