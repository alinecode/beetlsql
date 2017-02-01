package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DoubleTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		ResultSet rs = typePara.rs;
		double a = rs.getDouble(typePara.index);
		if(rs.wasNull()){
			if( typePara.target.isPrimitive()){
				return 0.0d;
			}else{
				return null;
			}
		}else{
			return a;
		}
		
	}

}
