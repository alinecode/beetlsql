package org.beetl.sql.core.meta;

import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.clazz.kit.ThreadSafeCaseInsensitiveHashMap;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.db.DBStyle;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

/**
 * 用于管理SQL数据库元信息，比如表，列，主键
 * @author xiandafu
 */
@Plugin
public class SchemaMetadataManager implements MetadataManager {

	protected ConnectionSource ds = null;
	protected ThreadSafeCaseInsensitiveHashMap tableInfoMap = null;
	protected TableDesc NOT_EXIST = new TableDesc("$$$NOT_EXIST","");
	protected DBStyle style;
	protected String defaultSchema;
	protected String defaultCatalog;
	protected String dbName = null;

	//是否检查列是否自增，目前通过异常判断驱动不支持
	boolean checkAuto = true;

	/**
	 * 真表-假表，用于可能的分表分库，比如user表不存在，user001,user00存在
	 * 因此user001->user
	 */

	ThreadSafeCaseInsensitiveHashMap tableVirtual = new ThreadSafeCaseInsensitiveHashMap();

	
	public SchemaMetadataManager(ConnectionSource ds, DBStyle style) {
		super();
		this.ds = ds;
		this.style = style;
		this.dbName = style.getName();
		//获取数据库schema
		initDefaultSchema();
	
	}

	public SchemaMetadataManager(ConnectionSource ds, String defaultSchema, String defaultCatalog, DBStyle style) {
		super();
		this.ds = ds;
		this.dbName = style.getName();
		this.defaultSchema = defaultSchema;
		this.defaultCatalog = defaultCatalog;


	}

	/***
	 * 表是否在数据库中
	 * 
	 * @param tableName
	 * @return
	 */
	@Override
	public boolean existTable(String tableName) {
		TableDesc table =getTableFromMap(tableName);
		return table!=null;
	}


	@Override
	public TableDesc getTable(String name){
		TableDesc table =getTableFromMap(name);		
		if(table==null){
			throw new BeetlSQLException(BeetlSQLException.TABLE_NOT_EXIST,"table \""+name+"\" not exist");
		}

		if(table.getCols().size()==0){
			table = initTable(table);
		}

		return table;
	}
	
	@Override
	public Set<String> allTable(){
		if(tableInfoMap ==null){
			this.initMetadata();
		}
		return this.tableInfoMap.keySet();
	}

	/**
	 * 非线程安全，只能开发模式下使用，比如在线代码生成
	 */
	public void refresh() {
		tableInfoMap = null;
		this.initMetadata();
	}



	protected TableDesc getTableFromMap(String tableName){
		TableDesc desc = null;
		if(tableInfoMap ==null){
			synchronized(this){
				if(tableInfoMap !=null){
					desc =  (TableDesc) tableInfoMap.get(tableName);
				}else{
					this.initMetadata();
					desc =  (TableDesc) tableInfoMap.get(tableName);
				}
				
			}
		}else{
			 desc = (TableDesc) tableInfoMap.get(tableName);
		}
	   
		if(desc==NOT_EXIST){
			return null;
		}else if(desc==null){
			int index = tableName.indexOf(".");
			if(index!=-1){
				//
				String schema = tableName.substring(0, index);
				String table = tableName.substring(index+1);
				return initOtherSchemaTable(schema,table);
				
			}else{
				return null;
			}
			
		}else{
			return desc;
		}
	}

	/**
	 * 获得某一个表的描述
	 * @param desc
	 * @return
	 */
	private  TableDesc  initTable(TableDesc desc){
		synchronized (desc){
			//先创建一个临时TableDesc，避免并发访问desc
			if(!desc.getCols().isEmpty()){
				//已经加载
				return desc;
			}
			TableDesc temp = new TableDesc(desc.getName(),desc.getRemark());
			temp.setRealTableName(desc.getRealTableName());
			temp.setCatalog(desc.getCatalog());
			temp.setSchema(desc.getSchema());

			Connection conn=null;
			ResultSet rs = null;
			String tableName = desc.getRealTableName()!=null?desc.getRealTableName():desc.getName();

			try {
				String catalog = temp.getCatalog();
				String schema = temp.getSchema();
				schema = this.getDbSchema(schema);
				conn =  ds.getMetaData();

				DatabaseMetaData dbmd =  conn.getMetaData();
				rs = dbmd.getPrimaryKeys(catalog,schema, tableName);


				while (rs.next()) {
					String idName=rs.getString("COLUMN_NAME");
					temp.addIdName(idName);
				}
				rs.close();

				rs = dbmd.getColumns(catalog,schema, tableName, "%");

				while(rs.next()){
					String colName = rs.getString("COLUMN_NAME");
					Integer sqlType = rs.getInt("DATA_TYPE");
					Integer size = rs.getInt("COLUMN_SIZE");
					Object o = rs.getObject("DECIMAL_DIGITS");
					String isNullable = (String)rs.getObject("IS_NULLABLE");



					Integer digit = null;
					if(o!=null){
						digit = ((Number)o).intValue();
					}

					String remark = rs.getString("REMARKS");
					ColDesc col = new ColDesc(colName,sqlType,size,digit,remark,isNullable);
					try{
						if(checkAuto){
							String  auto = rs.getString("IS_AUTOINCREMENT");
							if(auto!=null&&auto.equals("YES")){
								col.setAuto(true);
							}
						}

					}catch(SQLException ex){
						//某些驱动可能不支持
						checkAuto = false;
					}

					temp.addCols(col);
				}
				rs.close();
				moreInfo(temp);
				tableInfoMap.put(temp.getName(),temp);
				return temp;
			} catch (SQLException e) {
				throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
			}finally{
				close(conn);
			}
		}
	}
	
	private synchronized void initMetadata(){
		ThreadSafeCaseInsensitiveHashMap tempMap = new ThreadSafeCaseInsensitiveHashMap();
		Connection conn=null;
		try {
			conn =  ds.getMetaData();
			DatabaseMetaData dbmd =  conn.getMetaData();
			
			String catalog = this.defaultCatalog;
			String schema = this.defaultSchema;
			
			String namePattern = this.getTableNamePattern(dbmd);
			ResultSet rs = dbmd.getTables(catalog,schema, namePattern,
					getScope(catalog,schema));
			while(rs.next()){
				String  name = rs.getString("TABLE_NAME");
				//很多数据库的remarks默认并不能直接获取到，需要参考数据库厂商说明，通常需要配置JDBC链接一些特殊的参数
				String remarks = rs.getString("REMARKS");
				TableDesc desc = new TableDesc(name,remarks);
				desc.setSchema(this.defaultSchema);
				desc.setCatalog(catalog);
				tempMap.put(desc.getName(),desc);
				if(!tableVirtual.isEmpty()&& tableVirtual.containsKey(name)){
                    TableDesc newDesc = copyVirtualTable((String) tableVirtual.get(name),desc);
                    tempMap.put(newDesc.getName(),newDesc);
                }
			}

//			if(!this.virtuals.isEmpty()&&this.virtuals.containsKey())

			rs.close();
			this.tableInfoMap = tempMap;
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		}finally{
			close(conn);
		}
	}

	protected TableDesc copyVirtualTable(String virtualName, TableDesc desc){
		TableDesc newDesc = new TableDesc(virtualName,desc.getRemark());
		newDesc.setRealTableName(desc.getName());
		newDesc.setCatalog(desc.getCatalog());
		newDesc.setSchema(desc.getSchema());
		return newDesc;
	}

	/**
	 * 对于"xxx.yy"这种表名解析
	 * @param tablePrefix
	 * @param table
	 * @return
	 */
	protected TableDesc initOtherSchemaTable(String tablePrefix, String table){
		Connection conn=null;
		try {
			conn =  ds.getMetaData();
			DatabaseMetaData dbmd =  conn.getMetaData();

			//通过前缀判断catalog或者schema，依赖于数据库不同而不同
			String catalog = this.getDbCatalog(tablePrefix);
			String schema = this.getDbSchema(tablePrefix);
			
			ResultSet rs = null;
			rs = dbmd.getTables(catalog,schema, getDbTableName(table),
					getScope(catalog,schema));
		
			TableDesc desc  = null;
			while(rs.next()){
				String  name = rs.getString("TABLE_NAME");
				String remarks = rs.getString("REMARKS");
				desc = new TableDesc(name,remarks);
				desc.setSchema(tablePrefix);
				desc.setCatalog(catalog);
				tableInfoMap.put(tablePrefix+"."+table,desc);
			}
			rs.close();
			if(desc!=null){
				return desc ;
			}else{
				tableInfoMap.put(schema+"."+table,NOT_EXIST);
				return null;
			}
			
			
		} catch (SQLException e) {
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		}finally{
			close(conn);
		}
	}
	
	protected void close(Connection conn){
		this.ds.closeConnection(conn,null,false);
	}
	
	protected void initDefaultSchema(){

		if(defaultSchema==null){
			Connection conn = ds.getMetaData();
			try {
				setSchemaAndCatalogByConnection(conn);

			} catch (SQLException e) {
				throw new RuntimeException(e);
			}finally {
				this.close(conn);
			}
		}
	}

	/**
	 * 通过Conn来获取schema和catalog
	 * @param conn
	 * @throws SQLException
	 */
	protected void setSchemaAndCatalogByConnection(Connection conn) throws SQLException{
		try {
			this.defaultCatalog = conn.getCatalog();
		}catch(Throwable e) {
			// jdbc低版本不支持
		}
		
		try{
			this.defaultSchema =  conn.getSchema();
		}catch(Throwable e){
			// jdbc低版本不支持，设置默认值
			setDBDefaultSchema(conn);
		}
		
	}

	/**
	 * jdbc驱动不支持获取schema情况下，猜一下。 一般数据库的默认schema，比如postgrss的是"public"，oracle的是用户名
	 * @param conn
	 * @throws SQLException
	 * @see DBStyle#getDefaultSchema()
	 */
	protected  void setDBDefaultSchema(Connection conn)throws SQLException{
		if(dbName.equals("oracle")){
			defaultSchema = conn.getMetaData().getUserName();
		}else{
			defaultSchema = style.getDefaultSchema();
		}
	}

	/**
	 * 需要查看的元信息，包含表和视图
	 * @return
	 */
	protected String[] getScope(String catalog,String schema){
		return new String[] { "TABLE","VIEW" };
	}
	/**
	 * 
	 * 按照我理解，对于访问表xx.yyy, 不同数据库有不同的catalog和schema
	 */
	
	/**
	 * 
	 * @param namespace
	 * @return
	 */
	protected String getDbSchema(DatabaseMetaData dbmd,String namespace){
		if(dbName.equals("mysql")){
			return null;
		}else if(dbName.equals("oracle")){
			return namespace.toUpperCase();
		}else{
			return namespace;
		}
	}

	/**
	 * 对表定义进行修改
	 * @param tableDesc
	 * @see org.beetl.sql.core.nosql.SchemaLessMetaDataManager
	 */
	protected  void moreInfo(TableDesc tableDesc){
		return ;
	}

	protected String getTableNamePattern(DatabaseMetaData meta) throws SQLException{
		String p=meta.getDatabaseProductName();
		if(p.equalsIgnoreCase("mysql")){
			int c = meta.getDriverMajorVersion();
			if(c==6){
				return "%";
			}
		}
	
		return null;
	}
	protected String getDbSchema(String namespace){
		if(dbName.equals("mysql")){
			return null;
		}else if(dbName.equals("oracle")){
			return namespace.toUpperCase();
		}else{
			return namespace;
		}
	}
	
	private String getDbCatalog(String schema){
		if(dbName.equals("mysql")){
			return schema;
		}else{
			return null;
		}
	}
	
	private String getDbTableName(String name){
		if(dbName.equals("oracle")){
			return name.toUpperCase();
		}else if(dbName.equals("h2")){
			//假设h2数据库没有schema
			return null;
		}
		else{
			return name;
		}
	}

	/**
	 * 真表和假表
	 * @return
	 */
	public Map<String, String> getTableVirtual() {
		return tableVirtual;
	}

	@Override
	public void addTableVirtual(String realTable, String virtual){
		this.tableVirtual.put(realTable,virtual);
		if(this.tableInfoMap ==null){
			return ;
		}

		if(this.tableInfoMap.containsKey(realTable)){
			//所有表信息加载，但还未加载具体表信息，加载时候会根据这个关系创建一个TableDesc
			return;
		}


		TableDesc desc = (TableDesc)this.tableInfoMap.get(realTable);
		TableDesc  virutalTableDesc = new TableDesc(virtual,desc.getRemark());
		virutalTableDesc.setRealTableName(realTable);
		return ;


	}

}
