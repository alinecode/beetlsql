package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.jpa.SpringBoot;
import org.openjdk.jmh.annotations.Setup;

public class Jpa extends BaseBenchmark {

	@Setup
	public void init() {

		SpringBoot springBoot = new SpringBoot();
		springBoot.init();
		service = springBoot.getService();

	}
}
