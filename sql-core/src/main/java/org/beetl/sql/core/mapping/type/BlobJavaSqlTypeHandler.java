package org.beetl.sql.core.mapping.type;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobJavaSqlTypeHandler extends JavaSqlTypeHandler {
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		Blob a = rs.getBlob(typePara.index);
		return a;
	}

	public void setParameter(PreparedStatement ps, Object obj, int index)throws SQLException {
		ps.setBlob(index,(Blob)obj);
	}
}
