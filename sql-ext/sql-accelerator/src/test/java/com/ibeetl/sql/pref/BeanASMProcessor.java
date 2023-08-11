package com.ibeetl.sql.pref;

public class BeanASMProcessor {
	public void setValue(int index,Object obj,Object attrValue){
		TestBean testBean = (TestBean)obj;
		switch (index){
			case 1:testBean.setCol1((String)attrValue);break;
			case 2:testBean.setCol2((String)attrValue);break;
			case 3:testBean.setCol3((String)attrValue);break;
			case 4:testBean.setCol4((String)attrValue);break;
			case 5:testBean.setCol5((String)attrValue);break;
			case 6:testBean.setCol6((String)attrValue);break;
			case 7:testBean.setCol7((String)attrValue);break;
			case 8:testBean.setCol8((String)attrValue);break;
		}

	}
}
