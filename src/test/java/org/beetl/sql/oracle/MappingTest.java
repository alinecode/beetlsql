package org.beetl.sql.oracle;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.beetl.sql.OracleConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.OracleStyle;
import org.junit.Before;
import org.junit.Test;

public class MappingTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/");
		manager = new SQLManager(new OracleStyle(), loader, new OracleConnectoinSource());
	}

	

	@Test
	public void addMap() throws Exception {
		OracleType type = new OracleType();
		type.setId(1l);
		type.setDate1(new Date(1));
		type.setSt(new Timestamp(121212));
		type.setImage(new byte[]{1,2,3,5});
		type.setText2("ok12312322");
//		type.setMoney(3.2334);
//		type.setText("123");
//		type.setText2("456");
//		type.setDDouble(1.22323);
//		type.setDFloat(new Float(1.2));
		
		manager.insert(OracleType.class, type);
		
	}
	
	@Test
	public void selectMap() throws Exception {
		List<OracleType> list = manager.all(OracleType.class);
		System.out.println(list);
	}
}
