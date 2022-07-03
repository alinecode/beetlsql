package org.beetl.sql.core.mapping.type;


import org.beetl.sql.clazz.kit.LobKit;

import java.io.Reader;
import java.sql.*;

public class CharArrayTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		int index = typePara.index;
		if (typePara.dbName.equals("oracle")) {
			int type = typePara.meta.getColumnType(index);
			switch (type) {
				case Types.CLOB: {
					Clob clob = rs.getClob(index);
					if (clob == null) {
						return null;
					}
					Reader r = clob.getCharacterStream();
					return LobKit.getString(r).toCharArray();
				}
				case Types.NCLOB: {
					NClob nclob = rs.getNClob(index);
					if (nclob == null) {
						return null;
					}
					Reader r = nclob.getCharacterStream();
					return LobKit.getString(r).toCharArray();
				}


				default:
					return rs.getString(index).toCharArray();

			}
		} else {
			return rs.getString(index).toCharArray();
		}

	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		//TODO 需要确认是否这样，还是有更好的办法
		String str = new String((char[]) obj);
		writeTypeParameter.getPs().setString(writeTypeParameter.getIndex(),str);
	}

	@Override
	public int jdbcType() {
		return Types.CLOB;
	}

}
