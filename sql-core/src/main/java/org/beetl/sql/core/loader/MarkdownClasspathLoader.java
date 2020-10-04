
package org.beetl.sql.core.loader;

import org.beetl.sql.clazz.SQLType;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.SQLSource;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从classpath系统加载sql模板，id应该格式是"xx.yyy",xx代表了文件名，yyy代表了sql标识 sql 模板格式如下：
 *
 * <pre>
 selectUser 
 ===

 select * from user 
</pre>
 * 
 * 可以重载此类，实现{@code #getParser}  用于解析其他格式文件，比如xml格式的sql文件
 * @author Administrator
 *
 *
 *
 */
@Plugin
public class MarkdownClasspathLoader extends AbstractClassPathSQLLoader {

	protected String sqlRoot = null;
	protected String charset ;

	/**
	 * 外部sql缓存
	 */
	protected  Map<SqlId, SQLSource> sqlSourceMap = new ConcurrentHashMap<SqlId, SQLSource>();

	protected  SQLSource EMPTY = new SQLSource();

	public MarkdownClasspathLoader(String root,String charset) {
		super();
		this.sqlRoot = root;
		this.charset = charset;

	}

	public MarkdownClasspathLoader(String root) {
		super();
		this.sqlRoot = root;
		this.charset = Charset.defaultCharset().name();

	}

	public MarkdownClasspathLoader() {
		this("sql");

	}

	@Override
	public SQLSource queryExternalSource(SqlId id) {
		SQLSource source = sqlSourceMap.get(id);
		if(source==EMPTY){
			return null;
		}
		if(source!=null){
			return source;
		}

		loadFromClassPath(id);
		source = sqlSourceMap.get(id);
		if(source==null){
			sqlSourceMap.put(id,EMPTY);
			return null;
		}
		return source;

	}


	@Override
	public boolean existExternalSource(SqlId id) {
		SQLSource source = queryExternalSource(id);
		if(source==null){
			return false;
		}
		return true;
	}

	/**
	 * 比较sql是否变化，比较sql所在的文件是否变化，如果变化，则认为sql变化，提示beetl重新解析sql语句
	 * @param id
	 * @return
	 */
	@Override
	public boolean isExternalSourceModified(SqlId id) {

		SQLSource source = this.sqlSourceMap.get(id);
		if(source==null){
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


	
	protected  Long getURLVersion(URL url){
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
	protected  void loadFromClassPath(SqlId id) {
        //读取root目录下的sql文件
		URL ins = this.getRootFile(id);
        readSqlFile(id,ins,true);
        //读取db目录下的sql文件，进行覆盖
        ins = this.getDBRootFile(id);
        readSqlFile(id,ins,false);

	}

    protected void readSqlFile(SqlId sqlId, URL url, boolean isRoot) {
		if(url==null){
			return ;
		}
		InputStream ins;
		try {
			ins = url.openStream();
		} catch (IOException e1) {
			return ;
		}

        if(ins == null){
        	return ;
		}
		String modelName = sqlId.getNamespace();
        long lastModified = getURLVersion(url);
        LinkedList<String> list = new LinkedList<String>();
        BufferedReader bf = null;
        try {
       
            bf = new BufferedReader(new InputStreamReader(ins,charset));
            //解析markdown,可以解析xml，需要实现SQLFileParser
            SQLFileParser parser =this.getParser(modelName,bf);
            SQLSource source = null;
	    		while((source=parser.next())!=null){
	    			source.sqlType = SQLType.UNKOWN;
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
            throw new IllegalStateException(e);
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    // 忽略
					e.printStackTrace();
                }
            }
        }
        return ;
    }
    

	/***
	 * 获取.md文件
	 * md文件需放在classpath下
	 * @param id
	 * @return
	 * @throws UnexpectedException 
	 */
	protected URL getRootFile(SqlId id){
		URL url = getFilePath(sqlRoot,id);
		return url;

	}

	protected URL getDBRootFile(SqlId id){
		String root = sqlRoot + "/" + dbs.getName();
		URL url = getFilePath(root,id);
        return url;
    }


    protected URL getFilePath(String root, SqlId id){
		String path = this.getPathBySqlId(id);
		String filePath0 =  root +"/"+path + ".sql";
		String filePath1 =  root +"/"+path + ".md";
		URL is = this.getFile(filePath0);
		if(is==null){
			is = this.getFile(filePath1);
			if(is==null){
				return null;
			}
		}
		return is;
	}


	private URL getFile(String filePath){
	    return classLoaderKit.loadResourceAsURL(filePath);

	}

	public String toString(){
		return this.sqlRoot;
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

	protected SQLFileParser getParser(String modelName,BufferedReader br) throws IOException{
		MarkdownParser parser = new MarkdownParser(modelName,br);
		return parser;
	}

	public String getSqlRoot() {
		return sqlRoot;
	}

	public void setSqlRoot(String sqlRoot) {
		this.sqlRoot = sqlRoot;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}

