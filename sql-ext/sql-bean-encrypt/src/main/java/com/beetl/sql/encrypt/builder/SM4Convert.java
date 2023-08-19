package com.beetl.sql.encrypt.builder;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.beetl.sql.encrypt.EncryptConfig;
import com.beetl.sql.encrypt.EncryptType;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SM4Convert implements AttributeConvert {


	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}

		SymmetricCrypto sm4 = new SymmetricCrypto("SM4", EncryptConfig.get(EncryptType.SM4).getBytes(StandardCharsets.ISO_8859_1));
		String encryptHex = sm4.encryptHex(value);
		return encryptHex;

	}
	@Override
	public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
		String value  = rs.getString(index);
		if(value==null){
			return null;
		}
		SymmetricCrypto sm4 = new SymmetricCrypto("SM4",EncryptConfig.get(EncryptType.SM4).getBytes(StandardCharsets.ISO_8859_1));
		String decryptData = sm4.decryptStr(value);
		return decryptData;
	}

}
