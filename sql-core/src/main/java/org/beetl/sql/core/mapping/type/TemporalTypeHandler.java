package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Types;

/**
 * 对于Temporal 子类来说，如果jdbc 支持，则使用jdbc实现转化
 */
public class TemporalTypeHandler extends JavaSqlTypeHandler{
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		return typePara.rs.getObject(typePara.index,typePara.target);
	}

	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setObject(writeTypeParameter.getIndex(),obj);
	}

	@Override
	public int jdbcType() {
		return Types.DATE;
	}
}
