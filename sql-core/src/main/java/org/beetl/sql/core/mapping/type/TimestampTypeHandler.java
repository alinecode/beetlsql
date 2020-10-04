package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {

		return typePara.rs.getTimestamp(typePara.index);

	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),(Timestamp) obj);
	}

}
