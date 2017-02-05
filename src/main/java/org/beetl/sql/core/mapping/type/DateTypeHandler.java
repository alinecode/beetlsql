package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DateTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		java.sql.Date a = typePara.rs.getDate(typePara.index);
		if(a!=null){
			return new java.util.Date(a.getTime());
		}else{
			return null;
		}
		
		
	}

}
