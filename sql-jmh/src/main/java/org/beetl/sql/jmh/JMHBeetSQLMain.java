package org.beetl.sql.jmh;

import org.beetl.sql.jmh.beetl.BeetlSQLService;
import org.beetl.sql.jmh.jdbc.JdbcService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 测试beetlsql和JDBC
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(0)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class JMHBeetSQLMain {
    JdbcService jdbcService = null;
    BeetlSQLService fastBeetlSQLService = null;
	BeetlSQLService beetlSQLService = null;

    @Setup
    public void init() {

        jdbcService = new JdbcService();
        jdbcService.init();

        fastBeetlSQLService = new BeetlSQLService();
        fastBeetlSQLService.init(true);


		beetlSQLService = new BeetlSQLService();
		beetlSQLService.init(false);
    }


    /*   JDBC,基准，有些方法性能飞快    */
    @Benchmark
    public void jdbcInsert() {
        jdbcService.addEntity();
    }

    @Benchmark
    public void jdbcSelectById() {
        jdbcService.getEntity();
    }

    @Benchmark
    public void jdbcExecuteJdbc() {
        jdbcService.executeJdbcSql();
    }
	@Benchmark
	public void jdbcGetAll() {
		jdbcService.getAll();
	}


    /*   Fast BeetlSQL    */
    @Benchmark
    public void fastBeetlsqlInsert() {
        fastBeetlSQLService.addEntity();
    }

    @Benchmark
    public void fastBeetlsqlSelectById() {
        fastBeetlSQLService.getEntity();
    }

    @Benchmark
    public void fastBeetlsqlLambdaQuery() {
        fastBeetlSQLService.lambdaQuery();
    }

    @Benchmark
    public void fastBeetlsqlExecuteJdbc() {
        fastBeetlSQLService.executeJdbcSql();
    }

    @Benchmark
    public void fastBeetlsqlExecuteTemplate() {
        fastBeetlSQLService.executeTemplateSql();
    }

    @Benchmark
    public void fastBeetlsqlFile() {
        fastBeetlSQLService.sqlFile();
    }

    @Benchmark
    public void fastBeetlsqlPageQuery() {
        fastBeetlSQLService.pageQuery();
    }


    @Benchmark
    public void fastBeetlsqlOne2Many() {
        fastBeetlSQLService.one2Many();
    }

    @Benchmark
    public void fastBeetlsqlComplexMapping() {
        fastBeetlSQLService.complexMapping();
    }
	@Benchmark
	public void fastBeetlsqlGetAll() {
		fastBeetlSQLService.getAll();
	}


	/*   BeetlSQL    */
	@Benchmark
	public void beetlsqlInsert() {
		beetlSQLService.addEntity();
	}

	@Benchmark
	public void beetlsqlSelectById() {
		beetlSQLService.getEntity();
	}

	@Benchmark
	public void beetlsqlLambdaQuery() {
		beetlSQLService.lambdaQuery();
	}

	@Benchmark
	public void beetlsqlExecuteJdbc() {
		beetlSQLService.executeJdbcSql();
	}

	@Benchmark
	public void beetlsqlExecuteTemplate() {
		beetlSQLService.executeTemplateSql();
	}

	@Benchmark
	public void beetlsqlFile() {
		beetlSQLService.sqlFile();
	}

	@Benchmark
	public void beetlsqlPageQuery() {
		beetlSQLService.pageQuery();
	}


	@Benchmark
	public void beetlsqlOne2Many() {
		beetlSQLService.one2Many();
	}

	@Benchmark
	public void beetlsqlComplexMapping() {
		beetlSQLService.complexMapping();
	}
	@Benchmark
	public void beetlsqlGetAll() {
		beetlSQLService.getAll();
	}

	public static void main(String[] args) throws RunnerException {

        Options opt = new
                OptionsBuilder()
                .include(JMHBeetSQLMain.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }



}
