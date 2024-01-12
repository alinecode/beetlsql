package org.beetl.sql.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;


import java.util.List;
@SqlResource("user")
public interface OrderLogMapper extends BaseMapper<OrderLog> {

	@Call("call test.logcount(?,?,?)")
	@Update
	void logcount(int id, @CallOutBean  OutHolder outHolder);

	@Call("call test.updateStu(?,?)")
	@Update
	int update(int id, @CallOutBean  OutHolder outHolder);

	@Call("{? = call hello(?)}")
	@Select
	String sayHello(String name);


	List<OrderLog> select(List<Long> ids);

	@Template("select * from order_log where status in ( ${join(status)} )")
	List<OrderLog> selectByStatus(List<String> status);
}
