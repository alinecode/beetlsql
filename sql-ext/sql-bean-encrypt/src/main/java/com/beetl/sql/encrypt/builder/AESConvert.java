package com.beetl.sql.encrypt.builder;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.beetl.sql.encrypt.EncryptConfig;
import com.beetl.sql.encrypt.EncryptType;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AESConvert implements AttributeConvert {



	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}

		SymmetricCrypto aes =getAes(cls,name);
		String encryptData = aes.encryptHex(value);
		return encryptData;

	}
	@Override
	public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
		String value  = rs.getString(index);
		if(value==null){
			return null;
		}
		SymmetricCrypto aes =getAes(cls,name);
		String decryptData = aes.decryptStr(value);
		return decryptData;
	}

	protected SymmetricCrypto getAes(Class cls, String name){
		String key  = EncryptConfig.get(EncryptType.AES);;
		byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key.getBytes(StandardCharsets.ISO_8859_1)).getEncoded();
		SymmetricCrypto aes = SecureUtil.aes(byteKey);
		return aes;
	}
}
