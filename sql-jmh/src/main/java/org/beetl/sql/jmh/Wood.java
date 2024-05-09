package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.wood.WoodService;
import org.openjdk.jmh.annotations.Setup;

public class Wood extends BaseBenchmark {

	@Setup
	public void init() {

		service = new WoodService();
		((WoodService)service).init();

	}
}
