package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanAsmCode;
import com.beetl.sql.pref.BeanPropertyWrite;
import com.beetl.sql.pref.BeanPropertyWriteFactory;
import org.beetl.ow2.asm.ClassReader;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class BeanASMTest {

	@Test
	public void testGen() throws Exception{
		Class beanClass = TestBean.class;
//		byte[] bs = BeanAsmCode.genCode(beanClass);
//		FileOutputStream fos = new FileOutputStream(new File("d:/temp/My.class"));
//		fos.write(bs);

		Object bean = new TestBean();
		BeanPropertyWrite beanPropertyWrite = BeanPropertyWriteFactory.getBeanPropertyWrite(beanClass);
		beanPropertyWrite.setValue(1,bean,"hello");
		System.out.println(bean.toString());
	}
}
