package org.beetl.sql.jmh;

import org.beetl.sql.jmh.beetl.BeetlSQLService;
import org.beetl.sql.jmh.easyquery.EasyQueryService;
import org.beetl.sql.jmh.flex.FlexInitializer;
import org.beetl.sql.jmh.jdbc.JdbcService;
import org.beetl.sql.jmh.jpa.SpringBoot;
import org.beetl.sql.jmh.jpa.SpringService;
import org.beetl.sql.jmh.mybatis.MyBatisSpringBoot;
import org.beetl.sql.jmh.mybatis.MyBatisSpringService;
import org.beetl.sql.jmh.wood.WoodService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 性能测试入口,数据是Throughput，越大越好
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class JMHMain {
    JdbcService jdbcService = null;
    BeetlSQLService beetlSQLService = null;
    SpringService springService = null;
    MyBatisSpringService myBatisSpringService = null;
    WoodService woodService = null;
    EasyQueryService easyQueryService = null;

    @Setup
    public void init() {

        jdbcService = new JdbcService();
        jdbcService.init();

        beetlSQLService = new BeetlSQLService();
        beetlSQLService.init();

//        SpringBoot springBoot = new SpringBoot();
//        springBoot.init();
//        springService = springBoot.getService();
//
//
//        MyBatisSpringBoot myBatisSpringBoot = new MyBatisSpringBoot();
//        myBatisSpringBoot.init();
//        myBatisSpringService = myBatisSpringBoot.getService();
//
        woodService = new WoodService();
        woodService.init();
//
//		//see https://gitee.com/mybatis-flex/mybatis-benchmark
//		FlexInitializer.init();
//		easyQueryService=new EasyQueryService();
//		easyQueryService.init();

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


//    /* mybatis */
//    @Benchmark
//    public void mybatisInsert() {
//        myBatisSpringService.addEntity();
//    }
//
//    @Benchmark
//    public void mybatisSelectById() {
//        myBatisSpringService.getEntity();
//    }
//
//    @Benchmark
//    public void mybatisLambdaQuery() {
//        myBatisSpringService.lambdaQuery();
//    }
//
//    @Benchmark
//    public void mybatisExecuteTemplate() {
//        myBatisSpringService.executeTemplateSql();
//    }
//
//    @Benchmark
//    public void mybatisFile() {
//        myBatisSpringService.sqlFile();
//    }
//
//    @Benchmark
//    public void mybatisPageQuery() {
//        myBatisSpringService.pageQuery();
//    }
//
//    @Benchmark
//    public void mybatisComplexMapping() {
//        myBatisSpringService.complexMapping();
//    }
//	@Benchmark
//	public void mybatisGetAll() {
//		myBatisSpringService.getAll();
//	}
//
//
//    /*   BeetlSQL    */
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
//
//
//   /*   Spring Data JPA    */
//    @Benchmark
//    public void jpaInsert() {
//        springService.addEntity();
//    }
//
//    @Benchmark
//    public void jpaSelectById() {
//        springService.getEntity();
//    }
//
//    @Benchmark
//    public void jpaExecuteJdbc() {
//        springService.executeJdbcSql();
//    }
//
//    /*实际上JPA并不支持template，但勉强用HQl来测试*/
//    @Benchmark
//    public void jpaExecuteTemplate() {
//        springService.executeTemplateSql();
//    }
//
//    @Benchmark
//    public void jpaOne2Many() {
//        springService.one2Many();
//    }
//
//    @Benchmark
//    public void jpaPageQuery() {
//        springService.pageQuery();
//    }
//	@Benchmark
//	public void jpaGetAll() {
//		springService.getAll();
//	}
//
    /*   Wood    */
    @Benchmark
    public void woodInsert() {
        woodService.addEntity();
    }

    @Benchmark
    public void woodSelectById() {
        woodService.getEntity();
    }

    @Benchmark
    public void woodLambdaQuery() {
        woodService.lambdaQuery();
    }

    @Benchmark
    public void woodExecuteJdbc() {
        woodService.executeJdbcSql();
    }

    @Benchmark
    public void woodExecuteTemplate() {
        woodService.executeTemplateSql();
    }

    @Benchmark
    public void woodFile() {
        woodService.sqlFile();
    }

    @Benchmark
    public void woodPageQuery() {
        woodService.pageQuery();
    }
	@Benchmark
	public void woodGetAll() {
		woodService.getAll();
	}
//
//	/* flex orm */
//	@Benchmark
//	public void flexSelectById() {
//		FlexInitializer.selectOne();
//	}
//
//	@Benchmark
//	public void flexInsert() {
//		FlexInitializer.insert();
//	}
//
//	@Benchmark
//	public void flexPageQuery() {
//		FlexInitializer.paginate();
//	}
//
//	@Benchmark
//	public void flexFile() {
//		FlexInitializer.sqlFile();
//	}
//
//	@Benchmark
//	public void flexJdbc() {
//		FlexInitializer.executeJdbcSql();
//	}
//
//	@Benchmark
//	public void flexExecuteTemplate() {
//		FlexInitializer.executeTemplateSql();
//	}
//
//	@Benchmark
//	public void flexComplexMapping() {
//		FlexInitializer.complexMapping();
//	}
//	@Benchmark
//	public void flexGetAll() {
//		FlexInitializer.getAll();
//	}
//
//	/*   easy-query    */
//	@Benchmark
//	public void easyQueryInsert() {
//		easyQueryService.addEntity();
//	}
//
//	@Benchmark
//	public void easyQuerySelectById() {
//		easyQueryService.getEntity();
//	}
//
//	@Benchmark
//	public void easyQueryLambdaQuery() {
//		easyQueryService.lambdaQuery();
//	}
//
//	@Benchmark
//	public void easyQueryExecuteJdbc() {
//		easyQueryService.executeJdbcSql();
//	}
//
////	@Benchmark
////	public void easyQueryExecuteTemplate() {
////		easyQueryService.executeTemplateSql();
////	}
////
////	@Benchmark
////	public void easyQueryFile() {
////		easyQueryService.sqlFile();
////	}
//
//	@Benchmark
//	public void easyQueryPageQuery() {
//		easyQueryService.pageQuery();
//	}
//
//
//	@Benchmark
//	public void easyQueryOne2Many() {
//		easyQueryService.one2Many();
//	}
//
//	@Benchmark
//	public void easyQueryComplexMapping() {
//		easyQueryService.complexMapping();
//	}
//	@Benchmark
//	public void easyQueryGetAll() {
//		easyQueryService.getAll();
//	}
	public static void main(String[] args) throws RunnerException {

//          test();
        Options opt = new
                OptionsBuilder()
                .include(JMHMain.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    /**
     * 先单独运行一下保证每个测试都没有错误
     */
    public static void test() {
        JMHMain jmhMain = new JMHMain();
        jmhMain.init();
        for (int i = 0; i < 3; i++) {
            Method[] methods = jmhMain.getClass().getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Benchmark.class) == null) {
                    continue;
                }
                try {

                    method.invoke(jmhMain, new Object[0]);

                } catch (Exception ex) {
                    throw new IllegalStateException(" method " + method.getName(), ex);
                }

            }
        }

    }


}
