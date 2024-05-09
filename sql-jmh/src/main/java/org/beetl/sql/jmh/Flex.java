package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.flex.FlexInitializer;
import org.beetl.sql.jmh.flex.FlexService;
import org.openjdk.jmh.annotations.Setup;

public class Flex extends BaseBenchmark {


	@Setup
	public void init() {

		FlexInitializer.init();
		service = new FlexService();

	}
}
