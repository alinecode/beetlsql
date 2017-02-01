package org.beetl.sql.core.mapping.type;

import java.io.Reader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.beetl.sql.core.kit.LobKit;

public class StringTypeHandler extends JavaSqlTypeHandler {

	@Override
	public Object getValue(TypeParameter typePara) throws SQLException{
		ResultSet rs = typePara.rs;
		int index = typePara.index;
		if(typePara.dbName.equals("oracle")){
			int type = typePara.meta.getColumnType(index);
			switch(type){
			case   java.sql.Types.CLOB:{
				Reader r =	rs.getClob(index).getCharacterStream();
				return LobKit.getString(r);
				}
			case Types.NCLOB:{
				Reader r =	rs.getNClob(index).getCharacterStream();
				return LobKit.getString(r);
			}
			
		
			default:
				return rs.getString(index);
			
			}
		}else{
			return rs.getString(index);
		}
		
	}

}
