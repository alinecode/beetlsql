package org.beetl.sql.core.mapping.type;

import java.sql.*;

public class BlobJavaSqlTypeHandler extends JavaSqlTypeHandler {
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		Blob a = rs.getBlob(typePara.index);
		return a;
	}

	@Override
	public int jdbcType() {
		return Types.BLOB;
	}

	public void setParameter(PreparedStatement ps, Object obj, int index)throws SQLException {
		ps.setBlob(index,(Blob)obj);
	}
}
