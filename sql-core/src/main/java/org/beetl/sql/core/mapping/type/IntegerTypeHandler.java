package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	static Integer defaultValue = 0;

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		int a = rs.getInt(typePara.index);
		if(a!=0){
			//先判断0有助于提升性能
			return a;
		}else if(rs.wasNull()){
			if (typePara.isPrimitive()) {
				return defaultValue;
			} else {
				return null;
			}
		}else{
			return a;
		}


	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setInt(writeTypeParameter.getIndex(),(Integer)obj);
	}

	@Override
	public int jdbcType() {
		return Types.INTEGER;
	}

}
