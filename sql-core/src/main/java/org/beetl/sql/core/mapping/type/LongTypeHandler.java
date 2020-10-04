package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LongTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	//Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;

		long a = rs.getLong(typePara.index);
		if (a != 0) {
			//先判断0有助于提升性能
			return a;
		} else if (rs.wasNull()) {
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
		return 0L;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setLong(writeTypeParameter.getIndex(),(Long)obj);
	}

}
