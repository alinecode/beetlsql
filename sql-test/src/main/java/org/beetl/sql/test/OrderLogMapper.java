package org.beetl.sql.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.Call;
import org.beetl.sql.mapper.annotation.CallIndex;
import org.beetl.sql.mapper.annotation.CallOutBean;
import org.beetl.sql.mapper.annotation.SqlResource;


import java.util.List;
@SqlResource("user")
public interface OrderLogMapper extends BaseMapper<OrderLog> {

	@Call("call test.selectStu(?,?)")
	List<OrderLog> callSample(@CallIndex(1) int id,@CallOutBean  OutHolder outHolder);
}
