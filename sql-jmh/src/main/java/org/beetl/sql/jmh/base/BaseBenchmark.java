package org.beetl.sql.jmh.base;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public abstract class  BaseBenchmark implements  BaseService{
	protected BaseService service = null;

	@Benchmark
	@Override
	public void addEntity() {
		service.addEntity();
	}

	@Override
	@Benchmark
	public Object getEntity() {
		return service.getEntity();
	}
	@Benchmark
	@Override
	public void lambdaQuery() {
		service.lambdaQuery();
	}
	@Benchmark
	@Override
	public void executeJdbcSql() {
		service.executeJdbcSql();
	}
	@Benchmark
	@Override
	public void executeTemplateSql() {
		service.executeTemplateSql();
	}
	@Benchmark
	@Override
	public void sqlFile() {
		service.sqlFile();
	}
	@Benchmark
	@Override
	public void one2Many() {
		service.one2Many();
	}
	@Benchmark
	@Override
	public void pageQuery() {
		service.pageQuery();
	}
	@Benchmark
	@Override
	public void complexMapping() {
		service.complexMapping();
	}
	@Benchmark
	@Override
	public void getAll() {
		service.getAll();
	}

}
