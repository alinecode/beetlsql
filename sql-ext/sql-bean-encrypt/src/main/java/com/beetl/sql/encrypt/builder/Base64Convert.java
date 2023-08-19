package com.beetl.sql.encrypt.builder;

import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Base64Convert implements AttributeConvert {
	Charset utf8  = Charset.forName("UTF-8");

	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}
		byte[] bs = java.util.Base64.getEncoder().encode(value.getBytes(utf8));
		return new String(bs,utf8);

	}
	@Override
	public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
		String value  = rs.getString(index);
		if(value==null){
			return null;
		}
		return new String(java.util.Base64.getDecoder().decode(value),utf8);
	}
}
