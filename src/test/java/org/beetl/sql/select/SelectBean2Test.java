package org.beetl.sql.select;

import java.util.List;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.pojo.SimpleBean;
import org.junit.Before;
import org.junit.Test;

public class SelectBean2Test {
	private SQLLoader loader;
	private SQLManager manager;
	
	@Before
	public void before(){
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
	}
	
	@Test
	public void select(){
		List<SimpleBean> list = manager.all(SimpleBean.class);
		for(SimpleBean bean:list){
			System.out.println(bean.getId()+":"+bean.get("name"));
		}
	}

}
