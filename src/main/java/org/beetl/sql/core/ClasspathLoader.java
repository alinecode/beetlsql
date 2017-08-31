
package org.beetl.sql.core;

import java.io.BufferedReader;
import java.io.File;
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
import org.beetl.sql.core.kit.MDParser;

/**
 * 从classpath系统加载sql模板，id应该格式是"xx.yyy",xx代表了文件名，yyy代表了sql标识 sql 模板格式如下：
 *
 * <pre>
 selectUser 
 ===

 select * from user 
</pre>
 * 
 * 
 * @author Administrator
 *
 */
public class ClasspathLoader implements SQLLoader {

	protected String sqlRoot = null;

	protected String lineSeparator = System.getProperty("line.separator", "\n");

	protected  Map<String, SQLSource> sqlSourceMap = new ConcurrentHashMap<String, SQLSource>();

	protected DBStyle dbs = null;
	
	
	protected boolean autoCheck = true;
	
	protected String charset = null;
	
	protected SQLIdNameConversion sqlIdNameConversion = new DefaultSQLIdNameConversion();

	protected SQLSource NO_EXIST = new SQLSource();
	public  ClasspathLoader() {
		this("/sql");
	}
	public  ClasspathLoader(String root) {
		this(root ,new MySqlStyle());
	}
	public  ClasspathLoader(String root,DBStyle dbs) {
		this.sqlRoot = root;
		this.dbs = dbs;
	}

	@Override
	public SQLSource getSQL(String id) {
		SQLSource ss = this.tryLoadSQL(id);
		
		return ss;
	}
	
	private SQLSource tryLoadSQL(String id){
		SQLSource ss = sqlSourceMap.get(id);	
		boolean hasLoad = false ;
		if (ss == null) {
			loadSql(id);
			hasLoad = true;
		}
		if(!hasLoad&&this.autoCheck&&isModified(id)){
			loadSql(id);
		}
		
		//处理完后再次获取
		ss = sqlSourceMap.get(id);
		if(ss==null){
			sqlSourceMap.put(id, this.NO_EXIST);
			return null;
		}else if(ss==this.NO_EXIST){
			return null;
		}else{
			return ss;
		}
		
	}
	
	@Override
	public boolean isModified(String id) {
		SQLSource source = this.sqlSourceMap.get(id);
		if(source!=null&&source instanceof SQLTableSource&&source==NO_EXIST ){
			return false;
		}
		
		long oldRootVersion = source.getVersion().root;
		long oldDbVersion = source.getVersion().db;
		
		
        //如果db目录中有sql文件，直接使用db目录的文件判断版本（root中的文件会被db中的覆盖）
		URL root = this.getRootFile(id);
		URL db = this.getDBRootFile(id);
		
		if(getURLVersion(root)!=oldRootVersion||getURLVersion(db)!=oldDbVersion){
			//如果root目录和db目录只要有一个变化，都认为sql文件变化，重新加载
			return true;
		}else{
			return false ;
		}
		
	}
	
	protected static Long getURLVersion(URL url){
		if(url==null){
			return  0l;
		}
		
		if(url.getProtocol().equals("file")){
			String path = url.getFile();
			return new File(path).lastModified();
		}else{
			//其他协议，比如jar。
			return 0l;
		}
		
		
	}
	
	public boolean exist(String id){
		return this.tryLoadSQL(id)!=null;
	}
	
	@Override
	public void addSQL(String id, SQLSource source) {		
		sqlSourceMap.put(id, source);
		
	}

	/***
	 *  考虑到跨数据库支持，ClasspathLoader加载SQL顺序如下：
		首先根据DBStyle.getName() 找到对应的数据库名称，然后在ROOT/dbName 下找对应的sql，
		如果ROOT/dbName 文件目录不存在，或者相应的sql文件不存在，再搜索ROOT目录下的sql文件。
		如mysql 里查找user.select2,顺序如下：
		- 先找ROOT/mysql/user.sql 文件，如果有此文件，且包含了select2，则返回此sql语句，
        - 如果没有，下一步查找ROOT/mysql/user.md,如果有此文件，且包含了slect2，则返回sql语句
        - 如果没有，下一步查找ROOT/user.sql,如果有此文件，且包含了slect2，则返回sql语句
		- 如果没有，下一步查找ROOT/user.md,如果有此文件，且包含了slect2，则返回sql语句
		- 都没有，抛错，告诉用户未在ROOT/,或者ROOT/mysql 下找到相关sql
	 * 
	 * @return
	 */
	private boolean loadSql(String id) {
        //读取root目录下的sql文件
		URL ins = this.getRootFile(id);
        boolean rootResult;
		rootResult = readSqlFile(id,ins,true);
        //读取db目录下的sql文件，进行覆盖
        ins = this.getDBRootFile(id);
        boolean dbResult;
        dbResult = readSqlFile(id,ins,false);
        if(rootResult || dbResult){
            return true;
        }else {
        	
        		return false;
        }
	}

    private boolean readSqlFile(String id,URL url,boolean isRoot) {
    		if(url==null){
    			return false;
    		}
    		InputStream ins;
		try {
			ins = url.openStream();
		} catch (IOException e1) {
			return false;
		}
        String modelName = id.substring(0, id.lastIndexOf(".") + 1);
        if(ins == null) return false ;
        String filePath = sqlIdNameConversion.getPath(id);
      
        long lastModified = getURLVersion(url);
       
        
        LinkedList<String> list = new LinkedList<String>();
        BufferedReader bf = null;
        try {
       
            bf = new BufferedReader(new InputStreamReader(ins,charset));
            MDParser parser = new MDParser(modelName,bf);
            SQLSource source = null;
	    		while((source=parser.next())!=null){
	    			SQLFileVersion version = new SQLFileVersion();
	    			version.url = url;
	    			 if(isRoot){
	    	        		version.root = lastModified;
	    	        }else{
	    	        		version.db = lastModified;
	    	        }
	    			source.setVersion(version);
	    			sqlSourceMap.put(source.getId(), source);
	    		}
          
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
    
    
    private void buildSql(LinkedList<String> list,StringBuilder sql){
    	 while (!list.isEmpty()) {
    		 String s = list.pollFirst();
    		 if(s.startsWith("```")||s.startsWith("~~~")){
    			 //since 2.7.10,认为是beetlsql的块状符号
    			 continue ;
    		 }
             sql.append(s+lineSeparator);
         }
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
	 * md文件需放在classpath下
	 * @param id
	 * @return
	 * @throws UnexpectedException 
	 */
	private URL getRootFile(String id){
		String path = this.sqlIdNameConversion.getPath(id);
        String filePath0 = sqlRoot + "/" + path + ".sql";
		String filePath1 = sqlRoot + "/" + path + ".md";

		URL is = this.getFile(filePath0, id);
        if(is==null){
			is = this.getFile(filePath1, id);
            if(is==null){
                return null;
            }
        }
		return is;
	}

    private URL getDBRootFile(String id){
    	String path = this.sqlIdNameConversion.getPath(id);
        String filePath0 = sqlRoot + "/" + dbs.getName() + "/" + path + ".sql";
		String filePath1 = sqlRoot + "/" + dbs.getName() + "/" + path + ".md";
        URL is = this.getFile(filePath0, id);
        if(is==null){
            is = this.getFile(filePath1, id);
            if(is==null){
                return null;
            }
        }
        return is;
    }

	private URL getFile(String filePath, String id){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = null;
//		InputStream is  = null;
		if(loader!=null){
			url = loader.getResource(filePath);
			if(url!=null){
				return url;
			}else{
				url = this.getClass().getResource(filePath);
				return url;
			}
		}else{
			url = this.getClass().getResource(filePath);
			return url;
		}
		
		
	}
	
	
	
	@Override
	public boolean isAutoCheck() {
		return this.autoCheck;
	}
	
	@Override
	public void setAutoCheck(boolean check) {
		this.autoCheck = check;
		
	}
	

	public DBStyle getDbs() {
		return dbs;
	}
	public void setDbs(DBStyle dbs) {
		this.dbs = dbs;
	}
	@Override
	public String getCharset() {
		
		return charset;
	}
	@Override
	public void setCharset(String charset) {
		this.charset = charset;
		
	}
	
	public String toString(){
		return this.sqlRoot;
	}
	@Override
	public void setSQLIdNameConversion(SQLIdNameConversion sqlIdNc) {
		this.sqlIdNameConversion = sqlIdNc;
		
	}
	@Override
	public void setDbStyle(DBStyle dbStyle){
		this.dbs = dbStyle;
	}
	
	public static class SQLFileVersion{
		public URL url;
		//根目录下sql文件版本
		public long root=0l;
		//具体db下的
		public long db=0l;
		
		
		public boolean isModified(SQLFileVersion newVersion){
			if(newVersion.root!=root||newVersion.db!=db){
				return true;
			}else{
				return false;
			}
		}
		
	}
	
	
}

