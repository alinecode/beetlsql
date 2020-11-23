package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Time;

public class TimeTypeHandler extends JavaSqlTypeHandler {

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {

		return typePara.rs.getTime(typePara.index);
	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setTime(writeTypeParameter.getIndex(),(Time) obj);
	}

}
