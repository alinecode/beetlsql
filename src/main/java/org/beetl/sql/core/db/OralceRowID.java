package org.beetl.sql.core.db;

import java.sql.SQLException;

import oracle.sql.ROWID;

public class OralceRowID extends Number {
	oracle.sql.ROWID id = null;
	public OralceRowID(Object o){
		id = (ROWID)o;
	}
	@Override
	public int intValue() {
		try {
			return id.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long longValue() {
		try {
			return id.longValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float floatValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double doubleValue() {
		throw new UnsupportedOperationException();
	}

}
