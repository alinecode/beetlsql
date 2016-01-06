package org.beetl.sql.core.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.SQLManager;


public class MetadataManager {

	private ConnectionSource ds = null;
	Map<String,TableDesc> map = null;
//	TableDesc NOT_EXIST = new TableDesc("$NOT_EXIST","");
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
	public boolean existTable(String tableName) {
		TableDesc t = getTable(tableName);
		return t!=null;
	}




	public TableDesc getTable(String name){
		TableDesc table =getTableFromMap(name);		
		if(table==null){
			throw new BeetlSQLException(BeetlSQLException.TABLE_NOT_EXIST,"table \""+name+"\" not exist");
		}
		
		if(table.getMetaCols().size()==0){
			table = initTable(name);
		}
		return table;
	}
	
	public Set<String> allTable(){
		if(map==null){
			this.initMetadata();
		}
		return this.map.keySet();
	}
	
	private TableDesc getTableFromMap(String tableName){
		String name = tableName.toUpperCase();
		if(map==null){
			synchronized(this){
				if(map!=null) return map.get(name);
				this.initMetadata();
			}
		}
		return map.get(name);
	}
	
	private  TableDesc  initTable(String tableName){
		TableDesc desc =this.getTableFromMap(tableName);
		if(desc.getMetaCols().size()!=0){
			return desc ;
		}
		synchronized (desc){
			if(desc.getMetaCols().size()!=0){
				return desc ;
			}
			Connection conn=null;
			ResultSet rs = null;
			try {
				conn =  ds.getMaster();
				DatabaseMetaData dbmd =  conn.getMetaData();
				if(this.sm.getDbStyle().getName().equals("postgres")){
					String sql = "SELECT a.attname, format_type(a.atttypid, a.atttypmod) AS data_type " 
						+ " FROM   pg_index i "
						+" JOIN   pg_attribute a ON a.attrelid = i.indrelid "
						+ " AND a.attnum = ANY(i.indkey) "
						+" WHERE  i.indrelid = ?::regclass "
						+" AND    i.indisprimary";
					java.sql.PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, desc.getMetaName());
					rs =ps.executeQuery();
					int count = 0;
					while(rs.next()){
						count++;
						String metaIdName=rs.getString("attname");
						desc.setIdName(metaIdName);
					}
					rs.close();ps.close();
					//多个主键 下个版本再做
					if(count>1) throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR);
					
					
				}else{
					rs = dbmd.getPrimaryKeys(null, "%", desc.getMetaName());
					
					int count = 0;
					while (rs.next()) {
						count++;
						String metaIdName=rs.getString("COLUMN_NAME");
						
						desc.setIdName(metaIdName);
					}
					rs.close();
					//多个主键 下个版本再做
					if(count>1) throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR);
					
				}
				
				
				rs = dbmd.getColumns(null, "%", desc.getMetaName(), "%");
				while(rs.next()){
					String colName = rs.getString("COLUMN_NAME");
					Integer sqlType = rs.getInt("DATA_TYPE");
					Integer size = rs.getInt("COLUMN_SIZE");
					Object o = rs.getObject("DECIMAL_DIGITS");
					Integer digit = null;
					if(o!=null){
						digit = ((Number)o).intValue();
					}
					
					String remark = rs.getString("REMARKS");
					ColDesc col = new ColDesc(colName,sqlType,size,digit,remark);
					desc.addCols(col);
				}
				rs.close();
				return desc;
				
			} catch (SQLException e) {
				throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
			}finally{
				close(conn);
			}
		}
		
		
	}
	
	private synchronized void initMetadata(){
		if(map!=null) return ;
		map = new ConcurrentHashMap<String,TableDesc>();
		Connection conn=null;
		try {
			conn =  ds.getMaster();
			DatabaseMetaData dbmd =  conn.getMetaData();
			
			ResultSet rs = dbmd.getTables(null, "%", null,
					new String[] { "TABLE","VIEW" });
			while(rs.next()){
				String  name = rs.getString("TABLE_NAME");
				String remarks = rs.getString("REMARKS");
//				System.out.println("remarks="+remarks);
				TableDesc desc = new TableDesc(name,remarks);
				map.put(desc.getName(),desc);
			}
		
			
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		}finally{
			close(conn);
		}
	}
	
	
	private void close(Connection conn){
		try{
			if(!ds.isTransaction()){
				if(conn!=null)conn.close();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
