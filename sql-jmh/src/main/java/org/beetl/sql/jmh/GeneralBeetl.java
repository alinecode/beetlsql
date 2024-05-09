package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.beetl.BeetlSQLService;
import org.openjdk.jmh.annotations.Setup;

public class GeneralBeetl extends BaseBenchmark {



	@Setup
	public void init() {

		service = new BeetlSQLService();
		((BeetlSQLService)service).init(false);
	}
}
