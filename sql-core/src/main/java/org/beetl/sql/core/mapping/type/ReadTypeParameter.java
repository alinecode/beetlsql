package org.beetl.sql.core.mapping.type;

import lombok.Data;
import org.beetl.sql.core.SqlId;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * ResetSet 转化成 class的辅助对象
 * @author xiandafu
 */
@Data
public class ReadTypeParameter {
	public String dbName;
	public Class target;
	public ResultSet rs;
	public ResultSetMetaData meta;
	public int index;
	public SqlId sqlId;

	public ReadTypeParameter(SqlId sqlId, String dbName, Class target, ResultSet rs, ResultSetMetaData meta, int index) {
		super();
		this.dbName = dbName;
		this.target = target;
		this.rs = rs;
		this.meta = meta;
		this.index = index;
		this.sqlId = sqlId;
	}

	public boolean isPrimitive() {
		return target != null && target.isPrimitive();
	}

	public int getColumnType() throws SQLException {
		return meta.getColumnType(index);
	}

	public Object getObject() throws SQLException {
		return rs.getObject(index);
	}




}
