package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class FloatTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		ResultSet rs = typePara.rs;
		float a = rs.getFloat(typePara.index);
		if(rs.wasNull()){
			if( typePara.isPrimitive()){
				return getDefaultValue();
			}else{
				return null;
			}
		}else{
			return a;
		}
		
	}

	@Override
	public Object getDefaultValue() {
		// TODO Auto-generated method stub
		return 0.0f;
	}

}
