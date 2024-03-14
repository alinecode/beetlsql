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
import java.util.HashMap;
import java.util.Map;

public class MSSqlServerMetadataManager extends SchemaMetadataManager {
	private static Logger LOGGER = LoggerFactory.getLogger(MSSqlServerMetadataManager.class);

	boolean fetchRemark = true;
	Map<String, String> remarksmap = new HashMap();
	public MSSqlServerMetadataManager(ConnectionSource ds, DBStyle style) {
		this(ds,style,true);
	}
	public MSSqlServerMetadataManager(ConnectionSource ds, DBStyle style,boolean fetchRemark) {
		super(ds, style);
		this.fetchRemark = fetchRemark;
		remarksmap = tableInfo(ds);
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

			String tableComment =  remarksmap.get(tableDesc.getName());
			if(tableComment!=null){
				tableDesc.setRemark(tableComment);
			}

		}catch (SQLException sqlException){
			LOGGER.warn("获取列注释出错 "+sqlException.getMessage(),sqlException);
			//如果有权限问题，自动忽略
			return ;
		}

	}

	protected Map tableInfo(ConnectionSource ds){
		HashMap<String, String> remarksmap = new HashMap();
		if(!fetchRemark){
			return remarksmap;
		}

		String sqlremarks = "select tbl.table_name, prop.value as remarks from information_schema.tables tbl left join sys.extended_properties prop ON prop.major_id = object_id(tbl.table_schema + '.' + tbl.table_name) AND prop.minor_id = 0 AND prop.name = 'MS_Description' WHERE tbl.table_type = 'base table'";
		Connection conn = ds.getMasterConn();
		try{
			ResultSet rsremarks = conn.createStatement().executeQuery(sqlremarks);
			while (rsremarks.next()) {
				String tblname = rsremarks.getString("table_name");
				String tblremarks = rsremarks.getString("remarks");
				remarksmap.put(tblname, tblremarks);
			}
			rsremarks.close();
		}catch (SQLException exception){
			//如果不支持，或者权限问题，只打印异常
			LOGGER.warn("获取表注释出错 "+exception.getMessage(),exception);
		}
		finally {
			close(conn);
			return remarksmap;
		}


	}
}
