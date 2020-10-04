package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;

		double a = rs.getDouble(typePara.index);
		if(a!=0){
			//先判断0有助于提升性能
			return a;
		}else if(rs.wasNull()){
			if (typePara.isPrimitive()) {
				return getDefaultValue();
			} else {
				return null;
			}
		}else{
			return a;
		}



	}

	@Override
	public Object getDefaultValue() {
		return 0.0d;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setDouble(writeTypeParameter.getIndex(),(Double)obj);
	}



}
