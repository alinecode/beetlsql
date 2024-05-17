package org.beetl.sql.jmh;

import org.beetl.sql.jmh.base.BaseBenchmark;
import org.beetl.sql.jmh.beetl.BeetlSQLService;
import org.beetl.sql.jmh.dbvisitor.DBVisitorService;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class DBVisitor extends BaseBenchmark {



	@Setup
	public void init() {
		service = new DBVisitorService();
	}

//	public static void main(String[] args) throws RunnerException {
//		Options opt = new OptionsBuilder()
//			.include(DBVisitor.class.getSimpleName())
//			.forks(1)
//			.mode(Mode.SingleShotTime)
//			.build();
//
//		new Runner(opt).run();
//	}
}
