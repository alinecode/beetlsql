package org.beetl.sql.core.mapping.type;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class UtilDateTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {

		Timestamp ts  = typePara.rs.getTimestamp(typePara.index);
		if(ts==null){
			return null;
		}
		return new java.util.Date(ts.getTime());

	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		java.util.Date date = (java.util.Date)obj;
		if(date==null){
			writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),null);
		}else{
			Timestamp ts = new Timestamp(date.getTime());
			writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),ts);
		}

	}

	@Override
	public int jdbcType() {
		return Types.DATE;
	}

}
