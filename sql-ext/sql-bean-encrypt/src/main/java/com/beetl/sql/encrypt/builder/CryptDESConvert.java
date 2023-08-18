package com.beetl.sql.encrypt.builder;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.beetl.sql.encrypt.CryptType;
import com.beetl.sql.encrypt.EncryptConfig;
import org.beetl.sql.annotation.builder.AttributeConvert;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.core.ExecuteContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CryptDESConvert implements AttributeConvert {



	@Override
	public  Object toDb(ExecuteContext ctx, Class cls, String name, Object pojo) {

		String value= (String) BeanKit.getBeanProperty(pojo,name);
		if(value==null){
			return null;
		}

		SymmetricCrypto aes =getDES(cls,name);
		String encryptData = aes.encryptBase64(value);
		return encryptData;

	}
	@Override
	public  Object toAttr(ExecuteContext ctx, Class cls, String name, ResultSet rs, int index) throws SQLException {
		String value  = rs.getString(index);
		if(value==null){
			return null;
		}
		SymmetricCrypto aes =getDES(cls,name);
		String decryptData = aes.decryptStr(value);
		return decryptData;
	}

	protected DES getDES(Class cls, String name){
		String key = EncryptConfig.get(CryptType.DES);
		byte[] keys = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
		DES des = SecureUtil.des(keys);
		return des;
	}
}
