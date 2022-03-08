package org.beetl.sql.test;

import org.beetl.sql.mapper.BaseMapper;
import org.beetl.sql.mapper.annotation.SqlResource;


import java.util.List;
@SqlResource("user")
public interface OrderLogMapper extends BaseMapper<OrderLog> {
	List<OrderLog> select();
}
