package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Types;

public class ByteArrayTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		return typePara.rs.getBytes(typePara.index);
	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setBytes(writeTypeParameter.getIndex(),(byte[])obj);
	}

	@Override
	public int jdbcType() {
		return Types.BLOB;
	}

}
