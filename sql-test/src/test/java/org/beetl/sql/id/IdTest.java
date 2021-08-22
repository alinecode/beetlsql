package org.beetl.sql.id;

import org.beetl.sql.BaseTest;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.ext.UUIDAutoGen;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

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
		DeviceData data2 = new DeviceData();
		data2.setData("abce");
		String uuid = "2429fcc5-f47e-4189-abe1-16301d1e9828";
		data2.setId(uuid);
		sqlManager.insert(data2);



    }

	@Test
	public void autoIdTest(){
		String sql = sqlManager.getDbStyle().genInsert(Device.class).getTemplate();
		System.out.println(sql);

		Device data = new Device();
		data.setSn("abc");
		sqlManager.insert(data);
		Assert.assertNotNull(data.getId());

		Device data2 = new Device();
		sqlManager.insertTemplate(data2);
		Assert.assertNotNull(data2.getId());


	}


	@Test
	public void seqIdTest(){
		String sql = sqlManager.getDbStyle().genInsert(DeviceDetail.class).getTemplate();
		System.out.println(sql);

		DeviceDetail data = new DeviceDetail();
		data.setJson("{}");
		sqlManager.insert(data);

		Assert.assertNotNull(data.getId());


		DeviceDetail data2 = new DeviceDetail();
		data2.setJson("{}");
		data2.setId(9999);
		sqlManager.insert(data2);

	}


	@Test
	public void allTest(){

    	/*测试只提供主键，序列和自增自动生成*/
		BigData data = new BigData();
		data.setOrderId(1);
		data.setStatus(2);

		String sql = sqlManager.getDbStyle().genInsert(BigData.class).getTemplate();
		System.out.println(sql);

		sqlManager.insert(data);
		Assert.assertNotNull(data.getDataId());
		Assert.assertNotNull(data.getLabel());

		/*测试提供主键，并提供序列，自增自动生成*/
		BigData data2 = new BigData();
		data2.setOrderId(2);
		data2.setStatus(3);
		data2.setLabel(999);
		sqlManager.insert(data2);
		Assert.assertNotNull(data.getDataId());


		/*测试提供所有值*/
		BigData data3 = new BigData();
		data3.setOrderId(4);
		data3.setStatus(5);
		data3.setLabel(1999);
		data3.setDataId(123);
		sqlManager.insert(data3);
		Assert.assertNotNull(data.getDataId());


		BigData key = new BigData();
		key.setOrderId(data.getOrderId());
		key.setStatus(data.getStatus());

		BigData ret = sqlManager.unique(BigData.class,key);

		ret.setLabel(10);
		sqlManager.updateById(ret);




	}




}
