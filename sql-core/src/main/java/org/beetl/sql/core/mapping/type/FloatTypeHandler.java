package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FloatTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;

		float a = rs.getFloat(typePara.index);
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
		return 0.0f;
	}


	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setFloat(writeTypeParameter.getIndex(),(Float)obj);
	}

	@Override
	public int jdbcType() {
		return Types.FLOAT;
	}

}
