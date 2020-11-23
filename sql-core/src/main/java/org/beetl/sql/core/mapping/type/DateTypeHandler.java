package org.beetl.sql.core.mapping.type;

import org.beetl.sql.core.db.DBType;

import java.sql.SQLException;
import java.sql.Timestamp;

public class DateTypeHandler extends JavaSqlTypeHandler {

	//Override
	public Object getValue(ReadTypeParameter typePara) throws SQLException {
		Timestamp a = typePara.rs.getTimestamp(typePara.index);
		if (a != null) {
			return new java.util.Date(a.getTime());
		} else {
			return null;
		}
	}

	@Override
	public void setParameter(WriteTypeParameter writeTypeParameter,Object obj)throws SQLException {
		int dbType = writeTypeParameter.getDbType();
		// 兼容性修改：oralce 驱动 不识别util.Date
		if (dbType == DBType.DB_ORACLE || dbType == DBType.DB_POSTGRES || dbType == DBType.DB_DB2
				|| dbType == DBType.DB_SQLSERVER) {
			if (obj instanceof java.util.Date) {
				Timestamp ts = new Timestamp(((java.util.Date) obj).getTime());
				writeTypeParameter.getPs().setTimestamp(writeTypeParameter.getIndex(),ts);
				return ;
			}
		}
		//TODO,确认驱动如何设置java.util.Date,还是要转化为sql date？
		super.setParameter(writeTypeParameter,obj);

	}







}
