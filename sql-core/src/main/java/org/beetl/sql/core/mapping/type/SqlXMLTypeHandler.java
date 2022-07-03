package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Types;

public class SqlXMLTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		return typePara.rs.getSQLXML(typePara.index);
	}

	@Override
	public int jdbcType() {
		return Types.SQLXML;
	}

}
