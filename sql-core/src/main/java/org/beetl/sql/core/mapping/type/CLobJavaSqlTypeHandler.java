package org.beetl.sql.core.mapping.type;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CLobJavaSqlTypeHandler extends JavaSqlTypeHandler {
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		Clob a = rs.getClob(typePara.index);

		return a;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		//TODO 需要确认是否这样，还是有更好的办法
		writeTypeParameter.getPs().setClob(writeTypeParameter.getIndex(),(Clob)obj);
	}
}
