package org.beetl.sql.core.mapping.type;


import org.beetl.sql.clazz.kit.LobKit;

import java.io.Reader;
import java.sql.*;

public class StringTypeHandler extends JavaSqlTypeHandler {

	//Override
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
					return LobKit.getString(r);
				}
				case Types.NCLOB: {
					NClob nclob = rs.getNClob(index);
					if (nclob == null) {
						return null;
					}
					Reader r = nclob.getCharacterStream();
					return LobKit.getString(r);
				}


				default:
					return rs.getString(index);

			}
		} else {
			//认为其他数据库都支持直接通过jdbc获取字符串，如果不是这样，需要扩展StringTypeHandler
			return rs.getString(index);
		}

	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setString(writeTypeParameter.getIndex(),(String)obj);
	}

}
