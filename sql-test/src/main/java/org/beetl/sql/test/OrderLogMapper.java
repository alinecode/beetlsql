package org.beetl.sql.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.*;


import java.util.List;
@SqlResource("user")
public interface OrderLogMapper extends BaseMapper<OrderLog> {

	@Call("call test.selectStu(?,?)")
	List<OrderLog> callSample(int id, @CallOutBean  OutHolder outHolder);

	@Call("call test.updateStu(?,?)")
	@Update
	int update(int id, @CallOutBean  OutHolder outHolder);

	@Call("{? = call hello(?)}")
	@Select
	String sayHello(String name);
}
