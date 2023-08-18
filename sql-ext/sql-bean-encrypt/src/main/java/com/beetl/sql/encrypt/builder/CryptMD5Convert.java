package com.beetl.sql.encrypt.builder;

import cn.hutool.crypto.SecureUtil;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CryptMD5Convert implements AttributeConvert {

	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}
		String s = SecureUtil.md5(value);
		return s;

	}

}
