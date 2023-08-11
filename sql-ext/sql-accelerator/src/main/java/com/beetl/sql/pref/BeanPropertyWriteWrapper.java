package com.beetl.sql.pref;

import com.beetl.sql.pref.BeanPropertyWrite;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * 封装BeanPropertyWrite，让出错提示跟容易阅读
 */
public class BeanPropertyWriteWrapper extends BeanPropertyWrite {
	BeanPropertyWrite real;
	public BeanPropertyWriteWrapper(BeanPropertyWrite real){
		this.real = real;
	}
	@Override
	public void setValue(int index, Object obj, Object attrValue) {
		try{
			real.setValue(index,obj,attrValue);
		}catch (IllegalArgumentException ex){
			throw new BeetlSQLException(BeetlSQLException.ERROR,ex.getMessage());
		}
		catch (Exception ex){
			String attrName = findAttr(index,obj);
			if(ex instanceof ClassCastException){
				throw new BeetlSQLException(BeetlSQLException.ERROR,ex.getMessage()+" for attr "+attrName+" value:"+(attrValue!=null?attrValue.getClass():"null"));
			}
			throw new BeetlSQLException(BeetlSQLException.ERROR,ex.getMessage()+" for attr "+attrName+" value:"+(attrValue!=null?attrValue.getClass():"null"));
		}
	}

	protected String findAttr(int index, Object obj){
		PropertyDescriptor[] ps = null;
		try {
			ps = BeanKit.propertyDescriptors(obj.getClass());
		} catch (IntrospectionException e) {
			//不可能发生，要不到不了这里
			throw new RuntimeException(e);
		}
		if(ps.length<index){
			return "error index "+index;
		}
		PropertyDescriptor p = ps[index];
		return p.getName();


	}
}
