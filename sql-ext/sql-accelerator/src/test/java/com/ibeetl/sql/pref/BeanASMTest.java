package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanAsmCode;
import com.beetl.sql.pref.BeanPropertyAsm;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class BeanASMTest {

//	static {
//		BeanKit.JAVABEAN_STRICT = false;
//	}
	@Test
	public void testGen() throws Exception{

		Class beanClass = TestBean.class;
		byte[] bs = BeanAsmCode.genCode(beanClass);
		FileOutputStream fos = new FileOutputStream(new File("My.class"));
		fos.write(bs);

		Object bean = new TestBean();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);
		Integer input = 3;
		beanPropertyAsm.setValue(1,bean,input);
		Integer v = (Integer)beanPropertyAsm.getValue(1,bean);
		Assert.assertEquals(input,v);
		beanPropertyAsm.setValue(1,bean,input);

		beanPropertyAsm.setValue(3,bean,2.0);
		double ret = (Double)beanPropertyAsm.getValue(3,bean);
		Assert.assertEquals(2.0,ret,0.0001);

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
//			e.printStackTrace();
		}

		try{
			beanPropertyAsm.getValue(199,bean);
			Assert.fail();
		}catch (BeetlSQLException e){
//			e.printStackTrace();
		}

	}

	@Test
	public void testCastError() throws Exception{
		Class beanClass = TestBean2.class;


		Object bean = new TestBean2();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);


		try{
			beanPropertyAsm.setValue(1,bean,"1");
			Assert.fail();
		}catch (BeetlSQLException e){
//			e.printStackTrace();
		}

		try{
			beanPropertyAsm.getValue(1,bean);
			Assert.fail();
		}catch (BeetlSQLException e){
//			e.printStackTrace();
		}

	}

	public class TestBean2 extends  TestBean{
		public Integer getCol1() {
			throw new RuntimeException("test");
		}

//		public void setCol1(Integer col1) {
//			this.col1 = col1;
//			return ;
//		}

		public TestBean2 setCol1(Integer col1) {
			this.col1 = col1;
			return this;
		}
	}
}
