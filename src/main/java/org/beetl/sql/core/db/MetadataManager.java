package org.beetl.sql.core.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;

public class MetadataManager {

	private ConnectionSource ds = null;
	Map<String,Table> map = new ConcurrentHashMap<String,Table>();
	Table NOT_EXIST = new Table();
	
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
		Table t = getTable(tableName);
		return t!=null;
	}

	/****
	 * 字段是否在表中
	 * 
	 * @param tableName
	 * @param colName
	 * @return
	 */
	public boolean existColName(String tableName, String colName) {
		Table t = getTable(tableName);
		if(t==null) return false ;
		return t.cols.contains(colName);
	}

	/***
	 * 字段是否存在
	 * @param cls
	 * @param fieldName
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
	public String getIds(String tableName) {
		Table t = getTable(tableName);
		if(t==null) return null ;
		return t.idName;
	}
	
	private Table getTable(String name){
		Table table = map.get(name);
		if(table==null){
			table= initTable(name);
		}
		
		if(table==NOT_EXIST){
			throw new BeetlSQLException(BeetlSQLException.TABLE_NOT_EXIST,"table \""+name+"\" not exist");
		}
		return table;
	}
	
	private Table initTable(String tableName){
		Table table = new Table();
		table.name = tableName;
		
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
		
			ResultSet rs = dbmd.getTables(null, "%", tableName,
					new String[] { "TABLE" });
			if (!rs.next()) {
				map.put(tableName, NOT_EXIST);
				return NOT_EXIST;
			}
			
			rs = dbmd.getPrimaryKeys(null, "%", tableName);
			int count = 0;
			while (rs.next()) {
				count++;
				table.idName=rs.getString("COLUMN_NAME");
			}
			//多个主键 下个版本再做
			if(count>1) throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR);
			
			
			rs = dbmd.getColumns(null, "%", tableName, "%");
			while(rs.next()){
				String colName = rs.getString(4);
				table.cols.add(colName);
			}
			rs.close();
			map.put(table.name, table);
			return table;
			
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		}finally{
			close(conn);
		}
	}
	
	private void close(Connection conn){
		try{
			conn.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	static class Table{
		public String name;
		// 默认为id
		public String idName="id";
		public List<String> cols = new ArrayList<String>();
	}
	
	public static void main(String[] args){
		
	}
}
