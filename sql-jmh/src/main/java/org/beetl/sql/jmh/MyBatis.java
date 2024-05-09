package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.mybatis.MyBatisSpringBoot;
import org.openjdk.jmh.annotations.Setup;

public class MyBatis extends BaseBenchmark {

	@Setup
	public void init() {

		MyBatisSpringBoot myBatisSpringBoot = new MyBatisSpringBoot();
		myBatisSpringBoot.init();
		service = myBatisSpringBoot.getService();

	}
}
