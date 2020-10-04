package org.beetl.sql.core.mapping.type;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	protected Boolean b = false;

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		boolean a = rs.getBoolean(typePara.index);
		if (rs.wasNull()) {
			if (typePara.isPrimitive()) {
				return b;
			} else {
				return null;
			}
		} else {
			return a;
		}
	}

	@Override
	public Object getDefaultValue() {
		return b;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setBoolean(writeTypeParameter.getIndex(),(Boolean)obj);
	}

}
