package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.jdbc.JdbcService;
import org.openjdk.jmh.annotations.Setup;

public class Jdbc extends BaseBenchmark {


	@Setup
	public void init() {

		service = new JdbcService();
		((JdbcService)service).init();

	}
}
