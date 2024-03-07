package org.beetl.sql.core.db;

import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.meta.SchemaMetadataManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MSSqlServerMetadataManager extends SchemaMetadataManager {
	boolean fetchRemark = false;
	public MSSqlServerMetadataManager(ConnectionSource ds, DBStyle style) {
		super(ds, style);
	}
	public MSSqlServerMetadataManager(ConnectionSource ds, DBStyle style,boolean fetchRemark) {
		super(ds, style);
		this.fetchRemark = fetchRemark;
	}

	public MSSqlServerMetadataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style,boolean fetchRemark) {
		super(ds, defaultSchema, defaultCatalog, style);
		this.fetchRemark =fetchRemark;
	}
	public MSSqlServerMetadataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
		super(ds, defaultSchema, defaultCatalog, style);
	}

	@Override
	protected void moreInfo(Connection conn, TableDesc tableDesc){
		if(!fetchRemark){
			return ;
		}
		try{
			String sqlremarks = "SELECT C.NAME AS column_name,EP.VALUE AS remarks FROM SYS.EXTENDED_PROPERTIES EP LEFT JOIN SYS.ALL_OBJECTS O ON EP.MAJOR_ID = O.OBJECT_ID LEFT JOIN SYS.SCHEMAS S ON O.SCHEMA_ID = S.SCHEMA_ID LEFT JOIN SYS.COLUMNS AS C ON EP.MAJOR_ID = C.OBJECT_ID AND EP.MINOR_ID = C.COLUMN_ID WHERE EP.NAME = 'MS_Description' AND O.NAME= ?  AND EP.MINOR_ID > 0";
			PreparedStatement ps = conn.prepareStatement(sqlremarks);
			ps.setString(1,tableDesc.getName());
			ResultSet rsremarks =ps.executeQuery();
			while (rsremarks.next()) {
				String colname = rsremarks.getString("column_name");
				String colremarks = rsremarks.getString("remarks");
				tableDesc.getColDesc(colname).setRemark(colremarks);

			}
			rsremarks.close();
			ps.close();
		}catch (SQLException sqlException){
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,sqlException);
		}

	}
}
