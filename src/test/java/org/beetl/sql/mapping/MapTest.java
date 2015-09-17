/**
 * 
 */
package org.beetl.sql.mapping;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;

import org.beetl.sql.MySqlConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.mapping.QueryMapping;
import org.beetl.sql.core.mapping.handler.MapHandler;
import org.junit.Before;
import org.junit.Test;

/**
 * @author suxj
 *
 */
public class MapTest {

	private DBBase base;
	private Connection conn;
	SQLManager manager = null;
	ClasspathLoader loader;
	@Before
	public void setUp() throws Exception {
		loader = new ClasspathLoader("/sql/mysql");
		manager = new SQLManager(new MySqlStyle(), loader, new MySqlConnectoinSource());
		base = DBBase.getInstance();
		conn = base.getConn();
	}

	@Test
	public void query() {
		
		String sql = "select * from user3";
		ResultSet rs = base.getRs(conn, sql);
		QueryMapping query = QueryMapping.getInstance();
//		Map<String ,Object> map1 = query.query(rs, new MapHandler());//断点查看
//		Map<String ,Object> map2 = query.query(rs, new MapHandler(new HumpNameConversion()));//断点查看
		Map<String ,Object> map3 = query.query(rs, new MapHandler(new UnderlinedNameConversion(),manager));//断点查看
		
		//测试忽略key的大小写
		System.out.println(map3.get("tvarchar"));
		System.out.println(map3.get("tvarchar"));
		System.out.println(map3.get("TVARCHAR"));
	}
	
	@Test
	public void query2() {
		
	}
	

}
