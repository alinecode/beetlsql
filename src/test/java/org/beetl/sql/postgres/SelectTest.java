package org.beetl.sql.postgres;

import java.util.List;

import org.beetl.sql.OracleConnectoinSource;
import org.beetl.sql.PostgresConnectoinSource;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.HumpNameConversion;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.OracleStyle;
import org.beetl.sql.core.db.PostgresStyle;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Before;
import org.junit.Test;

public class SelectTest {
	private SQLLoader loader;
	private SQLManager manager;

	@Before
	public void before() {
		loader = new ClasspathLoader("/sql/");
		manager = new SQLManager(new PostgresStyle(), loader, new PostgresConnectoinSource(), new HumpNameConversion(),
				new Interceptor[] { new DebugInterceptor() });
	}

	@Test
	public void selectDept() throws Exception {

		Company2 dept = new Company2();
		dept.setName("hello");
		List list = manager.template(dept);
		//// List<Dept> list = manager.execute("select * from dept", Dept.class,
		//// new HashMap());
		System.out.println(list);

	}

	@Test
	public void selectPageDept() throws Exception {

		Company2 dept = new Company2();
		dept.setName("hello");
		List list = manager.template(dept, 1, 2);
		//// List<Dept> list = manager.execute("select * from dept", Dept.class,
		//// new HashMap());
		System.out.println(list);

	}

}