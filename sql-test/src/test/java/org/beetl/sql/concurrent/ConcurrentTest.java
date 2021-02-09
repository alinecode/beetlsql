package org.beetl.sql.concurrent;

import lombok.SneakyThrows;
import org.beetl.sql.BaseTest;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 系统启动之初的并发操作，sql语句初始化
 * @author xiandafu
 */
public class ConcurrentTest extends BaseTest {
	static ThreadPoolExecutor pool;

    @BeforeClass
    public static  void init(){
        initTable(testSqlFile);

		pool = new ThreadPoolExecutor(30, 30,
				//额外线程数
				1,//线程存活时间1分钟
				TimeUnit.MINUTES, new LinkedBlockingQueue<>(100));

    }


	@Test
	public void testTemplateById(){
    	//发生过，但现在已经解决了，在一个表有很多列的情况下
		Callable callable = new Callable() {
			@Override
			public Object call() throws Exception {
				User template = new User();
				template.setId(1);
				User user = sqlManager.templateOne(template);
				return user.getId();
			}
		};
		run(callable,1,100);
	}

	@Test
	public void testSelectById(){
		Callable callable = new Callable() {
			@Override
			public Object call() throws Exception {
				User user = sqlManager.unique(User.class,1);
				return user.getId();
			}
		};
		run(callable,1,100);
	}

    @Test
    public void testTemplateId(){
      Callable callable = new Callable() {
		  @Override
		  public Object call() throws Exception {
			  List<User> user = sqlManager.execute("select * from sys_user where id=1",User.class,null);
		  	  return user.get(0).getId();
		  }
	  };
		run(callable,1,100);
    }


    @SneakyThrows
	public void run(Callable callable,Object expected,int total )  {
		final AtomicBoolean success = new AtomicBoolean(true);
		CountDownLatch endLatch = new CountDownLatch(total);
    	for(int i=0;i<total;i++){
			pool.submit(new Runnable() {
				@Override
				public void run() {

					try {
						Object o = callable.call();
						if(!expected.equals(o)){
							success.set(false);;
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}finally {
						endLatch.countDown();
					}
				}
			});

		}
		endLatch.await();
		if(!success.get()){
			Assert.fail();
		}



	}





}
