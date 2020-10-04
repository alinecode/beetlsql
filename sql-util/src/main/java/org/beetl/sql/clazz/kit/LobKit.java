package org.beetl.sql.clazz.kit;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

/**
 * 读取lob字段的工具类
 * @author xiandafu
 */
public class LobKit {
	
	public static String getString(Reader rs) throws SQLException {
		char[] cs = new char[1024];
		StringBuilder sb = new StringBuilder();
		int len;
		try {
			while( ( len=rs.read(cs, 0, 1024))!=-1){
				sb.append(cs,0,len);
			}
			rs.close();
		} catch (IOException e) {
			throw new SQLException(e);
		}
		return sb.toString();
	}
}
