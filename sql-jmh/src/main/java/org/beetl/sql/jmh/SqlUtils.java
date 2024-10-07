package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.sqltutils.SqlUtilsService;
import org.openjdk.jmh.annotations.Setup;

public class SqlUtils extends BaseBenchmark {

	@Setup
	public void init() {
		service = new SqlUtilsService();
		((SqlUtilsService) service).init();
	}
}
