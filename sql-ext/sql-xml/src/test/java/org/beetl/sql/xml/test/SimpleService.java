package org.beetl.sql.xml.test;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SqlId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SimpleService {
	@Autowired
	SQLManager sqlManager;

	@Autowired
	ApplicationContext applicationContext;


	@Transactional
	public void test(){
		 Map map = new HashMap();
		 map.put("id",1);
		List<Map> list = sqlManager.select(SqlId.of("user","select"), Map.class,map);
		System.out.println(list);
	}


}
