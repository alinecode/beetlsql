package org.beetl.sql.core.mapping.type;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobJavaSqlTypeHandler extends JavaSqlTypeHandler {
	public Object getValue(TypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		Blob a = rs.getBlob(typePara.index);
		return a;
	}
}
