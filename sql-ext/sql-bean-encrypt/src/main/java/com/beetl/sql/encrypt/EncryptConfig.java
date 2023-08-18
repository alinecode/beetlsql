package com.beetl.sql.encrypt;

import com.beetl.sql.encrypt.CryptType;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置加密需要用的参数
 */
public class EncryptConfig {
	static Map<CryptType,String> prop = new HashMap<>();

	static {
		prop.put(CryptType.AES,"19780214xiandafu");
		prop.put(CryptType.DES,"19780214xiandafu");
	}
	public static synchronized  void config(CryptType key,String value){
		prop.put(key,value);
	}
	public static synchronized String get(CryptType key){
		return prop.get(key);
	}
}
