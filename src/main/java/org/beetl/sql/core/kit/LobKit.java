package org.beetl.sql.core.kit;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

public class LobKit {
	
	public static String getString(Reader rs) throws SQLException {
		char[] cs = new char[1024];
		StringBuilder sb = new StringBuilder();
		int len;
		try {
			while( ( len=rs.read(cs, 0, 1024))!=-1){
				sb.append(cs,0,len);
			}
		} catch (IOException e) {
			throw new SQLException(e);
		}
		return sb.toString();
	}
}
