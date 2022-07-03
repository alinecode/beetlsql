package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Types;

public class SqlDateTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		java.sql.Date a = typePara.rs.getDate(typePara.index);
		return a;

	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setDate(writeTypeParameter.getIndex(),(java.sql.Date)obj);
	}

	@Override
	public int jdbcType() {
		return Types.DATE;
	}

}
