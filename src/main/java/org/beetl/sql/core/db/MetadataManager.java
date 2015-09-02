package org.beetl.sql.core.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.beetl.sql.core.ConnectionSource;

public class MetadataManager {

	private ConnectionSource ds = null;
	
	public MetadataManager(ConnectionSource ds) {
		super();
		this.ds = ds;
	
	}

	public ConnectionSource getDs() {
		return ds;
	}

	public void setDs(ConnectionSource ds) {
		this.ds = ds;
	}

	/***
	 * 表是否在数据库中
	 * 
	 * @param tableName
	 * @return
	 */
	public boolean existtable(String tableName) {
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
		
			ResultSet rs = dbmd.getTables(null, "%", tableName,
					new String[] { "TABLE" });
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn);
		}
		
		return false;
	}

	/****
	 * 字段是否在表中
	 * 
	 * @param tableName
	 * @param colName
	 * @return
	 */
	public boolean existColName(String tableName, String colName) {
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
			ResultSet rs = dbmd.getColumns(null, "%", tableName, colName);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn);
		}
		return false;
	}

	/***
	 * 字段是否存在
	 * @param cls
	 * @param colName
	 * @return
	 */
	public boolean existPropertyName(Class<?> cls, String fieldName) {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 获取表中的id列表
	 * 
	 * @param tableName
	 * @return
	 */
	public List<String> getIds(String tableName) {
		List<String> idList = new ArrayList<String>();
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
			ResultSet rs = dbmd.getPrimaryKeys(null, "%", tableName);
			while (rs.next()) {
				idList.add(rs.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn);
		}
		if (idList.size() < 1) {
			return null;
		}
		return idList;
	}
	
	private void close(Connection conn){
		try{
			conn.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
