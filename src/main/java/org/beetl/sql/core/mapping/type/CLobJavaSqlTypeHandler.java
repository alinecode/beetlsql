package org.beetl.sql.core.mapping.type;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CLobJavaSqlTypeHandler extends JavaSqlTypeHandler {
	public Object getValue(TypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		Clob a = rs.getClob(typePara.index);

		return a;
	}
}
