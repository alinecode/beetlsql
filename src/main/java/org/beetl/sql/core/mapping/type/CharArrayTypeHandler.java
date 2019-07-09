package org.beetl.sql.core.mapping.type;

import org.beetl.sql.core.kit.LobKit;

import java.io.Reader;
import java.sql.*;

public class CharArrayTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		int index = typePara.index;
		if (typePara.dbName.equals("oracle")) {
			int type = typePara.meta.getColumnType(index);
			switch (type) {
				case java.sql.Types.CLOB: {
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

}
