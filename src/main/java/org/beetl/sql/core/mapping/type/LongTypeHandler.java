package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class LongTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		ResultSet rs = typePara.rs;
		long a = rs.getLong(typePara.index);
		if(rs.wasNull()){
			if( typePara.target.isPrimitive()){
				return 0L;
			}else{
				return null;
			}
		}else{
			return a;
		}
		
		
	}

}
