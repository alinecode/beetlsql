package org.beetl.sql.core;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.beetl.sql.BaseTest;
import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.SeqID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.H2Style;
import org.beetl.sql.entity.User;
import org.beetl.sql.ext.DebugInterceptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;

/**
 * 测试force操作
 */
public class ForceDataSourceTest extends BaseTest {
	static DataSource master ;
	static DataSource slave;
	static DataSource slave2;
	static boolean isMaster = false;
	static boolean isSlave = false;
	static SQLManager masterSlaveSqlManger;

	@BeforeClass
    public static void init(){

		master = createDatasource("master");
		slave = createDatasource("slave");
		slave2 = createDatasource("slave2");

		initData(master,testSqlFile);
		initData(slave,testSqlFile);
		initData(slave2,testSqlFile);

		ConnectionSource cs = ConnectionSourceHelper.getMasterSlave(master,new DataSource[]{slave,slave2});

		SQLManagerBuilder builder = new SQLManagerBuilder(cs);
		builder.setDbStyle(new H2Style());
		builder.setNc(new UnderlinedNameConversion());
		builder.setInters(new Interceptor[]{new MyInterceptor()});
		masterSlaveSqlManger = builder.build();

    }

    @Test
    public void forceMaster(){
		User user  = new User();
		user.setId(9);
		user.setName("test");
		masterSlaveSqlManger.forceDataSource(new DBRunner.MasterDBRunner<Object>(){

			@Override
			public Object run(SQLManager sm) {
				sm.insert(user);
				return null;
			}
		});
		Assert.assertTrue(isMaster);
		Assert.assertFalse(isSlave);
		Assert.assertNull(sqlManager.getDs().getForceDataSource());
		Integer dbId = user.getId();
		//数据在主库
		Assert.assertTrue(exit(master,User.class,dbId));
		Assert.assertFalse(exit(slave,User.class,dbId));

    }


	@Test
	public void forceSlave(){
		User user  = new User();
		user.setName("test");
		user.setId(19);
		masterSlaveSqlManger.forceDataSource(new DBRunner.SlaveDBRunner<Object>(){
			@Override
			public Object run(SQLManager sm) {
				sm.insert(user);
				return null;
			}
		});

		Assert.assertFalse(isMaster);
		Assert.assertTrue(isSlave);
		Assert.assertNull(sqlManager.getDs().getForceDataSource());

		Integer dbId = user.getId();
		//数据在从库
		Assert.assertTrue(exit(slave,User.class,dbId));
		Assert.assertFalse(exit(master,User.class,dbId));
	}

	@Test
	public void forceEachSlave(){
		User user  = new User();
		user.setName("test");
		user.setId(29);
		//遍历每个slave，并执行
		masterSlaveSqlManger.forceDataSource(new DBRunner.EachSlaveDbRunner(){
			@Override
			public Object run(SQLManager sm) {
				sm.insert(user);
				return null;
			}
		});

		Assert.assertFalse(isMaster);
		Assert.assertTrue(isSlave);
		Assert.assertNull(sqlManager.getDs().getForceDataSource());

		Integer dbId = user.getId();
		//数据在俩个从库里
		Assert.assertFalse(exit(master,User.class,dbId));
		Assert.assertTrue(exit(slave,User.class,dbId));
		Assert.assertTrue(exit(slave2,User.class,dbId));
	}




	@Test
	public void forceByPara(){
		Integer id = 1;

	   User user = masterSlaveSqlManger.forceDataSource(new DBRunner<User>(){

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
		Assert.assertNull(sqlManager.getDs().getForceDataSource());
	}


	protected boolean exit(DataSource ds,Class cls,Integer id){
		ConnectionSource cs = ConnectionSourceHelper.getSingle(ds);
		SQLManagerBuilder builder = new SQLManagerBuilder(cs);
		builder.setDbStyle(new H2Style());
		builder.setNc(new UnderlinedNameConversion());
		SQLManager temp = builder.build();
		Object entity =  temp.single(cls,id);
		return entity!=null;
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
			isSlave = ds==slave||ds==slave2;

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
		dataSource.setJdbcUrl("jdbc:h2:mem:"+name+";DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setMaximumPoolSize(5);
		dataSource.setPoolName(name);
		return dataSource;

	}




}
