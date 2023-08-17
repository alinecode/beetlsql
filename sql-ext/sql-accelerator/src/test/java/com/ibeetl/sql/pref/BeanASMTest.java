package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanPropertyAsm;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.junit.Assert;
import org.junit.Test;

public class BeanASMTest {

	@Test
	public void testGen() throws Exception{
		Class beanClass = TestBean.class;
//		byte[] bs = BeanAsmCode.genCode(beanClass);
//		FileOutputStream fos = new FileOutputStream(new File("My.class"));
//		fos.write(bs);

		Object bean = new TestBean();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);
		Integer input = 3;
		beanPropertyAsm.setValue(1,bean,input);
		Integer v = (Integer)beanPropertyAsm.getValue(1,bean);
		Assert.assertEquals(input,v);

	}

	@Test
	public void testError() throws Exception{
		Class beanClass = TestBean.class;


		Object bean = new TestBean();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);
		try{
			beanPropertyAsm.setValue(199,bean,"hello");
			Assert.fail();
		}catch (BeetlSQLException e){
			e.printStackTrace();
		}

		try{
			beanPropertyAsm.getValue(199,bean);
			Assert.fail();
		}catch (BeetlSQLException e){
			e.printStackTrace();
		}

	}

	@Test
	public void testCastError() throws Exception{
		Class beanClass = TestBean.class;


		Object bean = new TestBean();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);
//		try{
//			beanPropertyAsm.setValue(1,bean,1);
//			Assert.fail();
//		}catch (BeetlSQLException e){
//			e.printStackTrace();
//		}

		try{
			beanPropertyAsm.getValue(1,bean);
			Assert.fail();
		}catch (BeetlSQLException e){
			e.printStackTrace();
		}

	}
}
