package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanAsmCode;
import com.beetl.sql.pref.BeanPropertyAsm;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.junit.Assert;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;

public class BeanASMTest {

	static {
		BeanKit.JAVABEAN_STRICT = false;
	}
	@Test
	public void testGen() throws Exception{

		Class beanClass = TestBean.class;
//		byte[] bs = BeanAsmCode.genCode(beanClass);
//		FileOutputStream fos = new FileOutputStream(new File("My.class"));
//		fos.write(bs);
		PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(beanClass);
		int idIndex = -1;
		int col1= -1;
		int col3= -1;
		int byteCol = -1;
		int charCol = -1;
		int dataCol = -1;
		int i=0;

		for(PropertyDescriptor ps:propertyDescriptors){
			if(ps.getName().equals("id")){
				idIndex = i;
			}else if (ps.getName().equals("col1")){
				col1 = i;
			}else if(ps.getName().equals("col3")){
				col3 = i;
			}else if(ps.getName().equals("bytes")){
				byteCol = i;
			}else if(ps.getName().equals("chars")){
				charCol = i;
			}else if(ps.getName().equals("data")) {
				dataCol = i;
			}
			i++;
		}


		Object bean = new TestBean();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);

		Long id =12l;
		beanPropertyAsm.setValue(idIndex,bean,id);
		Long vId = (Long)beanPropertyAsm.getValue(idIndex,bean);
		Assert.assertEquals(vId,id);


		Integer input = 3;
		beanPropertyAsm.setValue(col1,bean,input);
		Integer v = (Integer)beanPropertyAsm.getValue(col1,bean);
		Assert.assertEquals(input,v);


		beanPropertyAsm.setValue(col3,bean,2.0);
		double ret = (Double)beanPropertyAsm.getValue(col3,bean);
		Assert.assertEquals(2.0,ret,0.0001);

		byte[] bs = new byte[]{1,2};
		char[] cs = new char[]{'a'};
		beanPropertyAsm.setValue(byteCol,bean,bs);
		beanPropertyAsm.setValue(charCol,bean,cs);
		Assert.assertEquals(bs,beanPropertyAsm.getValue(byteCol,bean));
		Assert.assertEquals(cs,beanPropertyAsm.getValue(charCol,bean));

		Integer[] arrayObject = new Integer[]{1,2};
		beanPropertyAsm.setValue(dataCol,bean,arrayObject);
		Assert.assertEquals(arrayObject,beanPropertyAsm.getValue(dataCol,bean));



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
		PropertyDescriptor[] propertyDescriptors = BeanKit.propertyDescriptors(beanClass);
		int colIndex  = -1;
		int i=0;
		for(PropertyDescriptor ps:propertyDescriptors){
			if(ps.getName().equals("col1")){
				colIndex = i;
			}
			i++;
		}

		Object bean = new TestBean2();
		BeanPropertyAsm beanPropertyAsm = BeanPropertyWriteFactory.getBeanProperty(beanClass);


		try{
			beanPropertyAsm.setValue(colIndex,bean,"1");
			Assert.fail();
		}catch (BeetlSQLException e){
			e.printStackTrace();
		}

		try{
			beanPropertyAsm.getValue(colIndex,bean);
			Assert.fail();
		}catch (BeetlSQLException e){
			e.printStackTrace();
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
