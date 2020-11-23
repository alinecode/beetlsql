package org.beetl.sql.core.mapping.type;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BigDecimalTypeHandler extends JavaSqlTypeHandler {

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		BigDecimal a = typePara.rs.getBigDecimal(typePara.index);
		return a;
	}

	public void setParameter(PreparedStatement ps, Object obj, int index)throws SQLException {
		ps.setBigDecimal(index,(BigDecimal)obj);
	}

}
