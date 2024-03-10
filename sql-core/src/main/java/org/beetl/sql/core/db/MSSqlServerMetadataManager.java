package org.beetl.sql.core.db;

import org.beetl.core.util.Log;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.meta.SchemaMetadataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MSSqlServerMetadataManager extends SchemaMetadataManager {
	private static Logger LOGGER = LoggerFactory.getLogger(MSSqlServerMetadataManager.class);

	boolean fetchRemark = true;
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
			//需要管理员权限
			String sqlremarks = "SELECT C.NAME AS column_name,EP.VALUE AS remarks FROM SYS.EXTENDED_PROPERTIES EP LEFT JOIN SYS.ALL_OBJECTS O ON EP.MAJOR_ID = O.OBJECT_ID LEFT JOIN SYS.SCHEMAS S ON O.SCHEMA_ID = S.SCHEMA_ID LEFT JOIN SYS.COLUMNS AS C ON EP.MAJOR_ID = C.OBJECT_ID AND EP.MINOR_ID = C.COLUMN_ID WHERE EP.NAME = 'MS_Description' AND O.NAME= ?  AND EP.MINOR_ID > 0";
			PreparedStatement ps = conn.prepareStatement(sqlremarks);
			ps.setString(1,tableDesc.getName());
			ResultSet rsremarks =ps.executeQuery();
			while (rsremarks.next()) {
				String colName = rsremarks.getString("column_name");
				String colRemarks = rsremarks.getString("remarks");
				tableDesc.getColDesc(colName).setRemark(colRemarks);

			}
			rsremarks.close();
			ps.close();
		}catch (SQLException sqlException){
			LOGGER.warn("获取列注释出错 "+sqlException.getMessage(),sqlException);
			//如果有权限问题，自动忽略
			return ;
		}

	}
}
