package com.ibeetl.sql.pref;

import com.beetl.sql.pref.BeanPropertyAsm;

import java.math.BigDecimal;

/**
 * 生成代码的样子
 */
public class BeanASMProcessor  extends BeanPropertyAsm {
	public void setValue(int index,Object obj,Object attrValue){
		TestBean testBean = (TestBean)obj;
		switch (index){
			case 0:testBean.setCol1((Integer)attrValue);break;
			case 1:testBean.setCol2((Integer)attrValue);break;
			case 2:testBean.setCol3((Double)attrValue);break;
			case 3:testBean.setCol4((Byte)attrValue);break;
			case 4:testBean.setCol5((Long)attrValue);break;
			case 5:testBean.setCol6((Short)attrValue);break;
			case 6:testBean.setCol7((String)attrValue);break;
			case 7:testBean.setCol8((Integer)attrValue);break;
			case 8:testBean.setBytes((byte[])attrValue );break;
			case 12:testBean.setData((Integer[])attrValue );break;
			default:throw throwException(index,obj);
		}

	}

	public Object getValue(int index,Object obj){
		TestBean testBean = (TestBean)obj;
		switch (index){
			case 1:return testBean.getCol1();
			case 2:return testBean.getCol2();
			case 3:return testBean.getCol3();
			case 4:return testBean.getBytes();
			default:throw throwException(index,obj);
		}
	}
}
