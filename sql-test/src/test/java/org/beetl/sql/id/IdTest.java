package org.beetl.sql.id;

import org.beetl.sql.BaseTest;
import org.beetl.sql.ext.UUIDAutoGen;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 测试mapper中使用@Root和Param注解
 */
public class IdTest extends BaseTest {
	static String file = "/db/id.sql";
    @BeforeClass
    public static void init(){
        initTable(file);
        sqlManager.addIdAutoGen("uuid",new UUIDAutoGen());
    }


    @Test
    public void uuidTest(){

		DeviceData data = new DeviceData();
		data.setData("abc");
		sqlManager.insert(data);
		Assert.assertNotNull(data.getId());
		System.out.println(data);

    }

	@Test
	public void autoIdTest(){

		Device data = new Device();
		data.setSn("abc");
		sqlManager.insert(data);
		Assert.assertNotNull(data.getId());
		System.out.println(data);

	}


	@Test
	public void seqIdTest(){

		DeviceDetail data = new DeviceDetail();
		data.setJson("{}");
		sqlManager.insert(data);
		Assert.assertNotNull(data.getId());
		System.out.println(data);

	}


	@Test
	public void allTest(){

		BigData data = new BigData();
		data.setOrderId(1);
		data.setStatus(2);
		sqlManager.insert(data);
		Assert.assertNotNull(data.getDataId());
		Assert.assertNotNull(data.getLabel());

		BigData key = new BigData();
		key.setOrderId(data.getOrderId());
		key.setStatus(data.getStatus());

		BigData ret = sqlManager.unique(BigData.class,key);

		ret.setLabel(10);
		sqlManager.updateById(ret);




	}




}
