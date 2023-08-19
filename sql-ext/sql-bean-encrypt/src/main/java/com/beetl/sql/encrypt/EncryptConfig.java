package com.beetl.sql.encrypt;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置加密需要用的参数
 */
public class EncryptConfig {
	static Map<EncryptType,String> prop = new HashMap<>();

	static {
		prop.put(EncryptType.AES,"19780214xiandafu");
		prop.put(EncryptType.DES,"19780214xiandafu");
		prop.put(EncryptType.SM4,"19780214xiandafu");
	}
	public static synchronized  void config(EncryptType key,String value){
		prop.put(key,value);
	}
	public static synchronized String get(EncryptType key){
		return prop.get(key);
	}
}
