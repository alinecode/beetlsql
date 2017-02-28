package org.beetl.sql.core.mapping.type;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class BigDecimalTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
//		Object c =  typePara.rs.getObject(typePara.index);
		BigDecimal a = typePara.rs.getBigDecimal(typePara.index);
		return a ;
		
	}

}
