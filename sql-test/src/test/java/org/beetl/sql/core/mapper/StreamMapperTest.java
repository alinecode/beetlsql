package org.beetl.sql.core.mapper;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.DSTransactionManager;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class StreamMapperTest extends BaseTest {
	@BeforeClass
	public static void init() {
		initTable(testSqlFile);
	}


	@Test
	public void mapperTest() throws SQLException {

		//stream操作必须再事务里使用，以期望能stream结束后，事物自动关闭数据库链接，而不是beetlsql
		DSTransactionManager.start();

		UserStreamDao dao = sqlManager.getMapper(UserStreamDao.class);
		long expected = dao.allCount();
		StreamData<User> streamData = dao.queryBySql(99999);

		final AtomicLong total = new AtomicLong();
		streamData.foreach(user -> {
			total.incrementAndGet();
		});
		Assert.assertEquals(expected, total.get());

		total.set(0);
		streamData = dao.queryByTemplate(99999);
		streamData.foreach(user -> {
			total.incrementAndGet();
		});
		Assert.assertEquals(expected, total.get());


		total.set(0);
		streamData = dao.streamTest();
		streamData.foreach(user -> {
			total.incrementAndGet();
		});
		Assert.assertEquals(expected, total.get());



		DSTransactionManager.commit();
	}
}