package com.beetl.sql.pref;

import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * 封装BeanPropertyWrite，让出错提示跟容易阅读
 */
public class BeanPropertyAsmWrapper extends BeanPropertyAsm {
	BeanPropertyAsm real;
	public BeanPropertyAsmWrapper(BeanPropertyAsm real){
		this.real = real;
	}
	@Override
	public void setValue(int index, Object obj, Object attrValue) {
		try{
			real.setValue(index,obj,attrValue);
		}catch (IllegalArgumentException ex){
			throw new BeetlSQLException(BeetlSQLException.ERROR,"不存在的属性索引 "+index+",class="+obj.getClass());
		}
		catch (Exception ex){
			PropertyDescriptor propertyDescriptor = findAttr(index,obj);
			String attrName = propertyDescriptor.getName();
			if(ex instanceof ClassCastException){
				throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,ex.getMessage()+" for attr "+attrName+" input value type:"+(attrValue!=null?attrValue.getClass():"null")+" but expected "+propertyDescriptor.getPropertyType());
			}
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,ex.getMessage()+" for attr "+attrName+" obj "+obj,ex);
		}
	}

	@Override
	public Object getValue(int index, Object obj) {
		try{
			return real.getValue(index,obj);
		}catch (IllegalArgumentException ex){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"不存在的属性索引 "+index+",class="+obj.getClass());
		}
		catch (Exception ex){
			String attrName = findAttr(index,obj).getName();
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,ex.getMessage()+" for attr "+attrName+",class="+obj.getClass(),ex);
		}
	}

	protected PropertyDescriptor findAttr(int index, Object obj){
		PropertyDescriptor[] ps = null;
		try {
			ps = BeanKit.propertyDescriptors(obj.getClass());
		} catch (IntrospectionException e) {
			//不可能发生，要不到不了这里
			throw new RuntimeException(e);
		}

		PropertyDescriptor p = ps[index];
		return p;


	}
}
