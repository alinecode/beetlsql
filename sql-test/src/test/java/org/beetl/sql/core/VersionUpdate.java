package org.beetl.sql.core;

import org.beetl.sql.BaseTest;
import org.beetl.sql.entity.ProductOrder;
import org.beetl.sql.entity.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Version和Logic 注解
 * @author xiandafu
 */
public class VersionUpdate extends BaseTest {
    @BeforeClass
    public static  void init(){
        initTable(testSqlFile);
    }

    @Test
    public void testInsertVersionById(){
		ProductOrder order = new ProductOrder();
		order.setCreateDate(new Date());
		order.setStatus(0);
		sqlManager.insert(order);

		ProductOrder dbOrder = sqlManager.unique(ProductOrder.class,order.getId());
		Assert.assertEquals(1L,dbOrder.getVersion().longValue());


    }

	@Test
	public void testInsertVersionTemplate(){
		ProductOrder order = new ProductOrder();
		order.setStatus(0);
		sqlManager.insertTemplate(order);
		ProductOrder dbOrder = sqlManager.unique(ProductOrder.class,order.getId());
		Assert.assertEquals(1L,dbOrder.getVersion().longValue());

	}

	@Test
	public void updateVersionById(){
		ProductOrder order = sqlManager.unique(ProductOrder.class,1);
		long version = order.getVersion();
		order.setCreateDate(new Date());
		order.setStatus(0);
		sqlManager.updateById(order);
		order = sqlManager.unique(ProductOrder.class,1);
		Assert.assertEquals(version+1,order.getVersion().longValue());

	}


	@Test
	public void updateVersionTemplateById(){
		ProductOrder order = sqlManager.unique(ProductOrder.class,1);
		long version = order.getVersion();
		order.setCreateDate(new Date());
		sqlManager.updateTemplateById(order);
		order = sqlManager.unique(ProductOrder.class,1);
		Assert.assertEquals(version+1,order.getVersion().longValue());

	}

	@Test
	public void logicDelete(){
		ProductOrder order = new ProductOrder();
		order.setCreateDate(new Date());
		order.setStatus(0);
		sqlManager.insert(order);
		sqlManager.deleteById(ProductOrder.class,order.getId());

		ProductOrder dbOrder = sqlManager.unique(ProductOrder.class,order.getId());
		Assert.assertEquals(1,dbOrder.getStatus().intValue());

	}


	@Test
	public void rawUpdateById(){
		ProductOrder order = sqlManager.unique(ProductOrder.class,1);
		long version = order.getVersion();
		order.setCreateDate(new Date());
		order.setStatus(0);
		sqlManager.updateRawById(order);
		order = sqlManager.unique(ProductOrder.class,1);
		Assert.assertEquals(version,order.getVersion().longValue());

	}



}
