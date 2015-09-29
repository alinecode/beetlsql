package org.beetl.sql.core.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.SQLManager;

public class MetadataManager {

	private ConnectionSource ds = null;
	Map<String,TableDesc> map = new ConcurrentHashMap<String,TableDesc>();
	TableDesc NOT_EXIST = new TableDesc();
	SQLManager sm = null;
	
	public MetadataManager(ConnectionSource ds,SQLManager sm) {
		super();
		this.ds = ds;
		this.sm = sm ;
	
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
		TableDesc t = getTable(tableName);
		return t!=null;
	}

	/****
	 * 字段是否在表中
	 * 
	 * @param tableName
	 * @param colName
	 * @return
	 */
	public boolean existColName(TableDesc table, String colName) {
		colName = colName.toLowerCase();
		return table.cols.contains(colName);
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
		TableDesc t = getTable(tableName);
		if(t==null) return null ;
		return t.idName;
	}
	
	public TableDesc getTable(String name){
		String indexName = name.toUpperCase();
		TableDesc table = map.get(indexName);

		if(table==null){
			table = initTable(name);
		}
		
		if(table==NOT_EXIST){
			throw new BeetlSQLException(BeetlSQLException.TABLE_NOT_EXIST,"table \""+name+"\" not exist");
		}
		return table;
	}
	
	private TableDesc initTable(String tableName){
	
		
		TableDesc table = new TableDesc();
		table.name = tableName.toUpperCase();
		String dbName = sm.getDbStyle().getName();
		if(dbName.equals("oracle")){
			tableName = tableName.toUpperCase();
		}else if(dbName.equals("postgres")){
			tableName = tableName.toLowerCase();
		}
		
		
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
			
			
			ResultSet rs = dbmd.getTables(null, "%", tableName,
					new String[] { "TABLE" });
			if (!rs.next()) {
				map.put(tableName.toUpperCase(), NOT_EXIST);
				return NOT_EXIST;
			}
			
			rs = dbmd.getPrimaryKeys(null, "%", tableName);
			int count = 0;
			while (rs.next()) {
				count++;
				table.idName=rs.getString("COLUMN_NAME").toLowerCase();
			}
			
			//多个主键 下个版本再做
			if(count>1) throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR);
			
			
			rs = dbmd.getColumns(null, "%", tableName, "%");
			while(rs.next()){
				String colName = rs.getString(4).toLowerCase();
				table.cols.add(colName);
			}
			rs.close();
//			//开发模式无需缓存table信息 ，以后再搞
//			if(sm.isProductMode()){
//				//map的key统一用大写
//				map.put(tableName.toUpperCase(), table);
//			}
			return table;
			
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		}finally{
			close(conn);
		}
	}
	
	private void close(Connection conn){
		try{
			if(!ds.isTransaction()){
				conn.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		
	}
}
