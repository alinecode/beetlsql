package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;

public class DefaultTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		return typePara.rs.getObject(typePara.index);
	}
	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setObject(writeTypeParameter.getIndex(),obj);
	}

}
