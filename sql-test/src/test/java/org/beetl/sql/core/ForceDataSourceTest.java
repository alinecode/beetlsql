package org.beetl.sql.core;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.entity.User;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * 测试插入操作,更复杂的id操作，参考IdTest
 */
public class ForceDataSourceTest extends BaseTest {
	static DataSource master ;
	static DataSource slave;
	static boolean isMaster = false;
	static boolean isSlave = false;

	@BeforeClass
    public static void init(){
        initTable(testSqlFile);
		master = createDatasource("master");
		slave = createDatasource("slave");
		System.out.println("master "+master);
		System.out.println("slave " +slave);
		ConnectionSource ds = ConnectionSourceHelper.getMasterSlave(master,new DataSource[]{slave});
		sqlManager.setDs(ds);
		sqlManager.setInters(new Interceptor[]{new MyInterceptor()});
    }

    @Test
    public void forceMaster(){
		sqlManager.forceDataSource(new DBRunner.MasterDBRunner<Object>(){

			@Override
			public Object run(SQLManager sm) {
				sm.unique(User.class,1);
				return null;
			}
		});
		Assert.assertTrue(isMaster);
		Assert.assertFalse(isSlave);
    }

	@Test
	public void forceSlave(){
		sqlManager.forceDataSource(new DBRunner.SlaveDBRunner<Object>(){

			@Override
			public Object run(SQLManager sm) {
				sm.unique(User.class,1);
				return null;
			}
		});
		Assert.assertFalse(isMaster);
		Assert.assertTrue(isSlave);
	}

	@Test
	public void forceByPara(){

		Integer id = 1;

	   User user = sqlManager.forceDataSource(new DBRunner<User>(){

			@Override
			protected DataSource getTargetDataSource(SQLManager sqlManager) {
				return id%2==0?sqlManager.getDs().getMasterSource():sqlManager.getDs().getSlaves()[0];
			}
			@Override
			public User run(SQLManager sm) {
				User user = sm.unique(User.class,id);
				return user ;
			}
		});
		Assert.assertFalse(isMaster);
		Assert.assertTrue(isSlave);
	}

	public abstract static class MyParaRunner extends  DBRunner{
		Integer id  = null;
		public MyParaRunner(Integer id){
			this.id = id;
		}

		@Override
		protected DataSource getTargetDataSource(SQLManager sqlManager) {
			return id%2==0?sqlManager.getDs().getMasterSource():sqlManager.getDs().getSlaves()[0];
		}

	}


	public static class MyInterceptor implements   Interceptor{

		@Override
		public void before(InterceptorContext ctx) {
			DataSource ds = ctx.getExecuteContext().sqlManager.getDs().getForceDataSource();
			isMaster = ds==master;
			isSlave = ds==slave;

		}

		@Override
		public void after(InterceptorContext ctx) {

		}

		@Override
		public void exception(InterceptorContext ctx, Exception ex) {

		}
	}

	public  static DataSource  createDatasource(String name) {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl("jdbc:h2:mem:dbtest;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setMaximumPoolSize(5);
		dataSource.setPoolName(name);
		return dataSource;

	}




}
