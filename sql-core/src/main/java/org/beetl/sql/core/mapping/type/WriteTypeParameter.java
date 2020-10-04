package org.beetl.sql.core.mapping.type;

import lombok.Data;
import org.beetl.sql.core.SqlId;

import java.sql.PreparedStatement;

@Data
public class WriteTypeParameter {
	Class target;
	String dbName;
	int dbType;
	PreparedStatement ps;
	int index;
	SqlId sqlId;

	public WriteTypeParameter(SqlId sqlId, String dbName, int dbType, Class target, PreparedStatement ps , int index) {
		super();
		this.dbName = dbName;
		this.target = target;
		this.target = target;
		this.ps = ps;
		this.index = index;
		this.sqlId = sqlId;
	}

	public boolean isPrimitive() {
		return target != null && target.isPrimitive();
	}




}
