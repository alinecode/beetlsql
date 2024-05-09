package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.easyquery.EasyQueryService;
import org.openjdk.jmh.annotations.Setup;

public class EasyQuery extends BaseBenchmark {


	@Setup
	public void init() {

		service=new EasyQueryService();
		((EasyQueryService)service).init();

	}
}
