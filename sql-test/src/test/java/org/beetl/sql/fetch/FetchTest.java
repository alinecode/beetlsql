package org.beetl.sql.fetch;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.entity.fetch.Customer;
import org.beetl.sql.entity.fetch.CustomerOrder;
import org.beetl.sql.entity.fetch.CustomerOrder2;
import org.beetl.sql.entity.fetch.CustomerOrder3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchTest extends BaseTest {
    @Before
    public void init(){

        super.initTable(testSqlFile);
    }


    @Test
    public void testUnique(){
        Customer user = sqlManager.single(Customer.class,1);
        Assert.assertNotNull(user);
        List<CustomerOrder> order = user.getOrder();
        Assert.assertNotNull(order);
        Assert.assertEquals(2,order.size());
        Customer customer = order.get(0).getCustomer();
        Assert.assertEquals(user.getId(),customer.getId());

		Customer notExistCustomer = sqlManager.single(Customer.class,5);
		Assert.assertNull(notExistCustomer);
    }


    @Test
    public void testOrderUnique(){
        CustomerOrder order = sqlManager.single(CustomerOrder.class,1);
        Assert.assertNotNull(order);
        Customer customer = order.getCustomer();
        Assert.assertNotNull(customer);
        Assert.assertEquals(1,customer.getId().intValue());
        List<CustomerOrder> orders = customer.getOrder();
        Assert.assertNotNull(orders);
        Assert.assertEquals(2,orders.size());
    }

	@Test
	public void testAllOrder(){
		List<CustomerOrder> orders = sqlManager.all(CustomerOrder.class);
		for(CustomerOrder order:orders){
			Assert.assertNotNull(order.getCustomer());
		}
	}


	@Test
	public void testFetchBySql(){
		List<CustomerOrder2> orders = sqlManager.all(CustomerOrder2.class);
		for(CustomerOrder2 order:orders){
			Assert.assertNotNull(order.getCustomer());
			Assert.assertNotNull(order.getCustomers());
		}
	}


	/**
	 * 验证动态配置是否映射，参考"sysOrder.md"
	 */
	@Test
	public void testEnable(){
    	Map map = new HashMap<>();
    	map.put("id",1);

		CustomerOrder3 customerOrder = sqlManager.selectSingle(SqlId.of("sysOrder","dynamicFetchOrder1"),map, CustomerOrder3.class);
		Assert.assertNotNull("not null",customerOrder.getCustomer());


		customerOrder = sqlManager.selectSingle(SqlId.of("sysOrder","dynamicFetchOrder2"),map, CustomerOrder3.class);
		Assert.assertNull("should  null",customerOrder.getCustomer());

    }


}
