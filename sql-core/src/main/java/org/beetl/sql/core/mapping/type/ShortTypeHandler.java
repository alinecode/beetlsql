package org.beetl.sql.core.mapping.type;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortTypeHandler extends JavaSqlTypeHandler implements PrimitiveValue {

	Short defaultValue = 0;

	//Override
	@Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		ResultSet rs = typePara.rs;
		short a = rs.getShort(typePara.index);
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
		return defaultValue;
	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		writeTypeParameter.getPs().setShort(writeTypeParameter.getIndex(),(Short)obj);
	}

}
