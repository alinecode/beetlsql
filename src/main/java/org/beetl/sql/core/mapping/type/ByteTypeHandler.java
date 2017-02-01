package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ByteTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		ResultSet rs = typePara.rs;
		short a = rs.getShort(typePara.index);
		if(rs.wasNull()){
			if( typePara.target.isPrimitive()){
				return 0;
			}else{
				return null;
			}
		}else{
			return a;
		}
		
	}

}
