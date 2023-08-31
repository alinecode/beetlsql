package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.entity.ProductOrder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 *  logic删除，考虑俩给模式
 * @author xiandafu
 */
public class LogicDeleteTest extends BaseTest {
	@BeforeClass
	public static void init() {
		initTable(testSqlFile);
	}


	@Test
	public void testLogicDelete() {

		ProductOrder order = new ProductOrder();
		order.setCreateDate(new Date());
		order.setStatus(0);
		sqlManager.insert(order);
		long total = sqlManager.allCount(ProductOrder.class);
		Assert.assertEquals(2, total);
		//逻辑删除
		sqlManager.deleteById(ProductOrder.class, order.getId());
		total = sqlManager.allCount(ProductOrder.class);
		//还是总是2
		Assert.assertEquals(2, total);


		ProductOrder dbOrder = sqlManager.unique(ProductOrder.class, order.getId());
		Assert.assertEquals(1L, dbOrder.getVersion().longValue());

		Query<ProductOrder> query = sqlManager.query(ProductOrder.class);
		query.andEq("id", order.getId());
		dbOrder = query.unique();
		Assert.assertEquals(1L, dbOrder.getVersion().longValue());

		//如下代码包测试逻辑删除部分,实际情况是需要配置QueryLogicDeleteEnable
		//删除生成的缓存
		sqlManager.refresh();
		sqlManager.setQueryLogicDeleteEnable(true);

		//考虑逻辑删除，查询不出来
		dbOrder = sqlManager.single(ProductOrder.class, order.getId());
		Assert.assertNull(dbOrder);

		query = sqlManager.query(ProductOrder.class);
		query.andEq("id", order.getId());
		dbOrder = query.single();
		Assert.assertNull(dbOrder);

		//逻辑删除不在查询范围内
		long newTotal = sqlManager.allCount(ProductOrder.class);
		Assert.assertEquals(1, newTotal);

		newTotal = sqlManager.all(ProductOrder.class).size();
		Assert.assertEquals(1, newTotal);


		ProductOrder template = new ProductOrder();
		template.setId(order.getId());
		int queryCount = sqlManager.template(template).size();
		Assert.assertEquals(0, queryCount);


		//恢复默认值
		sqlManager.refresh();
		sqlManager.setQueryLogicDeleteEnable(false);

	}


	@Test
	public void testQueryDelete() {
		ProductOrder order = new ProductOrder();
		order.setCreateDate(null);
		order.setStatus(1);
		sqlManager.insert(order);
		sqlManager.setQueryLogicDeleteEnable(false);
		LambdaQuery lambdaQuery = sqlManager.lambdaQuery(ProductOrder.class);

		ProductOrder newOrder = new ProductOrder();
		newOrder.setCreateDate(new Date());
		newOrder.setStatus(1);
		int ret =  lambdaQuery.andEq("id",order.getId()).desc("id").delete();

		System.out.println(sqlManager.all(ProductOrder.class));

	}
}
