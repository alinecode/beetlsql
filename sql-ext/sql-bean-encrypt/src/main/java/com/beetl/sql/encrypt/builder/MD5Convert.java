package com.beetl.sql.encrypt.builder;

import cn.hutool.crypto.SecureUtil;
import com.beetl.sql.encrypt.annotation.MD5;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.PropertyDescriptorWrap;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.ExecuteContext;

public class MD5Convert implements AttributeConvert {

	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {
		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}
		MD5 MD5 = BeanKit.getAnnotation(cls,name, MD5.class);
		String salt = null;
		if(StringKit.isNotBlank(MD5.salt())){
			salt = MD5.salt();
		}else if(StringKit.isNotBlank(MD5.saltProperty())){
			String targetPropertyName = MD5.saltProperty();
			PropertyDescriptorWrap wrap = BeanKit.getPropertyDescriptorWrapWithNull(cls,targetPropertyName);
			if(wrap==null){
				throw new BeetlSQLException(BeetlSQLException.MAPPING_ERROR,"MD5配置错误德尔属性 "+name+ " @"+cls.getName());
			}
			Object targetPropertyValue = wrap.getValue(pojo);
			salt=targetPropertyValue==null?"":targetPropertyValue.toString();
		}

		String s = getMD5(value,salt);
		return s;

	}

	protected String getMD5(String content,String salt){
		String s = SecureUtil.md5(content+salt);
		return s;
	}

}
