package org.beetl.sql.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;

/**
 * 从classpath系统加载sql模板，id应该格式是"xx.yyy",xx代表了文件名，yyy代表了sql标识 sql 模板格式如下：
 * 
 * ==selectUser * comment select * from user where .. * ==selectAgenyUser select
 * * from agencyUser where .. *
 * 
 * 
 * @author Administrator
 *
 */
public class ClasspathLoader implements SQLLoader {

	String sqlRoot = null;

	private String lineSeparator = System.getProperty("line.separator", "\n");

	private static Map<String, SQLSource> sqlSourceMap = new ConcurrentHashMap<String, SQLSource>();
	private static Map<String, Long> sqlSourceVersion = new ConcurrentHashMap<String, Long> ();

	private DBStyle dbs = null;
	
	private boolean autoCheck = true;

	public  ClasspathLoader() {
		this.sqlRoot = "/sql";
	}
	public  ClasspathLoader(String root) {
		this.sqlRoot = root;
		dbs = new MySqlStyle();
	}
	public  ClasspathLoader(String root,DBStyle dbs) {
		this.sqlRoot = root;
		this.dbs = dbs;
	}

	@Override
	public SQLSource getSQL(String id) {
		SQLSource ss = sqlSourceMap.get(id);		
		if (ss == null) {
			loadSql(id);
		}
		if(this.autoCheck&&isModified(id)){
			loadSql(id);
		}
		
		//处理完后再次获取
		ss = sqlSourceMap.get(id);
		return ss;
	}
	
	@Override
	public boolean isModified(String id) {
		int index = id.indexOf('.');
		if(index!=-1){
			String sqlName = id.substring(index);
			if(sqlName.startsWith("._gen")){
				return false;
			}
		}
		File file = this.getFile(id);
		if(file==null) return true;
		long lastModify = file.lastModified();
		Long oldVersion = sqlSourceVersion.get(id);
		if(oldVersion==null) return true;
		if(oldVersion!=lastModify){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean exist(String id){
		return loadSql(id);
	}
	
	@Override
	public void addGenSQL(String id, SQLSource source) {
		sqlSourceVersion.put(id, 0l); //never change
		sqlSourceMap.put(id, source);
		
	}

	/***
	 *  考虑到夸数据库支持，ClasspathLoader加载SQL顺序如下：
		首先根据DBStyle.getName() 找到对应的数据库名称，然后在ROOT/dbName 下找对应的sql，
		如果ROOT/dbName 文件目录不存在，或者相应的sql文件不存在，再搜索ROOT目录下的sql文件。
		如mysql 里查找user.select2,顺序如下：
		- 先找ROOT/mysql/user.md 文件，如果有此文件，且包含了select2，则返回此sql语句，
		- 如果没有，下一步查找ROOT/user.md,如果有此文件，且包含了slect2，则返回sql语句
		- 都没有，抛错，告诉用户未在ROOT/,或者ROOT/mysql 下找到相关sql
	 * 
	 * @param file
	 * @return
	 */
	private boolean loadSql(String id) {
		
		String modelName = id.substring(0, id.lastIndexOf(".") + 1);
		File file = this.getFile(id);
		if(file==null) return false ;
		InputStream ins  = null;
		try{
			ins = new FileInputStream(file);
		}catch(IOException ioe){
			throw new RuntimeException("id not found "+id,ioe);
		}
		
		long lastModified = file.lastModified();
		sqlSourceVersion.put(id, lastModified);
//		InputStream ins = this.getClass().getResourceAsStream(
//				sqlRoot + File.separator + modelName + "md");
		LinkedList<String> list = new LinkedList<String>();
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new InputStreamReader(ins));
			String temp = null;
			StringBuilder sql = null;
			String key = null;
			int lineNum = 0;
			int findLineNum = 0;
			while ((temp = bf.readLine()) != null) {
				lineNum++;
				if (temp.startsWith("===")) {// 读取到===号，说明上一行是key，下面是SQL语句
					
					if (!list.isEmpty() && list.size() > 1) {// 如果链表里面有多个，说明是上一句的sql+下一句的key
						String tempKey = list.pollLast();// 取出下一句sql的key先存着
						sql = new StringBuilder();
						key = list.pollFirst();
						while (!list.isEmpty()) {// 拼装成一句sql
							sql.append(list.pollFirst() + lineSeparator);
						}
						SQLSource source = new SQLSource(modelName + key,sql.toString());
						source.setLine(findLineNum);						
						sqlSourceMap.put(modelName + key, source);// 放入map
						list.addLast(tempKey);// 把下一句的key又放进来
						findLineNum = lineNum;
					}
				} else {
					list.addLast(temp);
				}
			}
			// 最后一句sql
			sql = new StringBuilder();
			key = list.pollFirst();
			while (!list.isEmpty()) {
				sql.append(list.pollFirst()+lineSeparator);
			}
			SQLSource source = new SQLSource(modelName + key,sql.toString());
			source.setLine(findLineNum);	
			sqlSourceMap.put(modelName + key,source
					);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public Map<String, SQLSource> getSqlSourceMap() {
		return sqlSourceMap;
	}

	public String getSqlRoot() {
		return sqlRoot;
	}
	
	public void setSqlRoot(String sqlRoot) {
		this.sqlRoot = sqlRoot;
	}
	/***
	 * 获取.md文件
	 * @param id
	 * @return
	 * @throws UnexpectedException 
	 */
	private File getFile(String id){
		String modelName = id.substring(0, id.lastIndexOf(".") + 1);
		String filePath = sqlRoot + "/" + dbs.getName() + "/" + modelName + "md";
		URL url = this.getClass().getResource(filePath);
		File file = null;
		
		if(url == null){
			url = this.getClass().getResource(sqlRoot + "/" + modelName + "md");
		}
		
		try {
			if(url==null) {
				throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "在"+sqlRoot+"和"+sqlRoot + "/" +dbs.getName()+"未找到[id="+id+"]相关的SQL");
			}
			
			file = new File(url.getFile());
			if(file == null){
				url = this.getClass().getResource(sqlRoot + "/" + modelName + "md");
				file = new File(url.getFile());
				if(file==null){
					throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "在"+sqlRoot+"和"+sqlRoot + "/" +dbs.getName()+"未找到[id="+id+"]相关的SQL");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file;
	}
	
	@Override
	public boolean isAutoCheck() {
		return this.autoCheck;
	}
	
	@Override
	public void setAutoCheck(boolean check) {
		this.autoCheck = check;
		
	}
	@Override
	public SQLSource getGenSQL(String id) {
		return this.sqlSourceMap.get(id);
	}
	
}
