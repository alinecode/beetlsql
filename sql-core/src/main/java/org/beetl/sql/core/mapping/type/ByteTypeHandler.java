package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		byte a = rs.getByte(typePara.index);
		if (rs.wasNull()) {
			if (typePara.isPrimitive()) {
				return getDefaultValue();
			} else {
				return null;
			}
		} else {
			return a;
		}

	}

	@Override
	public Object getDefaultValue() {
		return 0;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setByte(writeTypeParameter.getIndex(),(byte)obj);
	}

}
