package org.beetl.sql.clazz.kit;

import lombok.Data;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 *
 */
@Data
public class  PropertyDescriptorWrap{
	protected  PropertyDescriptor prop;
	protected int i;
	protected Method setMethod =null;
	public PropertyDescriptorWrap(Class c,PropertyDescriptor prop,int i){
		this.prop = prop;
		this.i = i;
		findSetMethod(c, prop);
	}

	public void setValue(Object o,Object value){
		if(setMethod==null){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"找不到\"写属性\""+ prop.getName()+" @"+o.getClass());
		}
		try {
			setMethod.invoke(o,value);
		}catch (Exception  ex){
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "属性赋值错误 "+ prop.getName()+" @"+o.getClass()+",Error="+ex.getMessage());
		}

	}

	public Object getValue(Object o){
		try{
			return prop.getReadMethod().invoke(o);
		} catch (Exception ex) {
			throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR, "属性取值错误 "+ prop.getName()+" @"+o.getClass()+",Error="+ex.getMessage());
		}
	}

	protected  Method findSetMethod(Class c,PropertyDescriptor p){
		Method method = p.getWriteMethod();
		if(method!=null){
			return  method;
		}

		if(!BeanKit.JAVABEAN_STRICT){
			String name = p.getReadMethod().getName();
			if(name.startsWith("is")){
				name = "set"+name.substring(2);
			}else{
				name = "set"+name.substring(3);
			}
			Class type = p.getPropertyType();
			try {
				//链式调用
				Method setMethod =c.getMethod(name,type);
				return setMethod;
			} catch (NoSuchMethodException e) {
				return null;
			}

		}else{
			return null;
		}
	}

}
