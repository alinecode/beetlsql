
package org.beetl.sql.core;

import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.UnexpectedException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private  Map<String, SQLSource> sqlSourceMap = new ConcurrentHashMap<String, SQLSource>();
	private  Map<String, Integer> sqlSourceVersion = new ConcurrentHashMap<String, Integer> ();

	private DBStyle dbs = null;
	
	
	private boolean autoCheck = true;

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
		
		if(ss==null){
			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL,"未能找到"+id+"对应的sql");
		}
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
        //如果db目录中有sql文件，直接使用db目录的文件判断版本（root中的文件会被db中的覆盖）
		InputStream is = this.getDBRootFile(id);
        if(is == null){
            //db目录中没有文件，使用root下的
            is = this.getRootFile(id);
        }
//		if(file==null) return true;
//		long lastModify = file.lastModified();
//		Long oldVersion = sqlSourceVersion.get(id);
//		if(oldVersion==null) return true;
//		if(oldVersion!=lastModify){
//			return true;
//		}else{
//			return false;
//		}
		if(is != null){
			Integer lastModify = is.hashCode();
			Integer oldVersion = sqlSourceVersion.get(id);
			if(oldVersion != null && oldVersion.equals(lastModify)){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean exist(String id){
		return this.tryLoadSQL(id)!=null;
	}
	
	@Override
	public void addGenSQL(String id, SQLSource source) {		
	
		sqlSourceVersion.put(id, 0); //never change
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
		InputStream ins = this.getRootFile(id);
        boolean rootResult = readSqlFile(id,ins);
        //读取db目录下的sql文件，进行覆盖
        ins = this.getDBRootFile(id);
        boolean dbResult = readSqlFile(id,ins);
        if(rootResult || dbResult){
            return true;
        }else {
            String modelName = id.substring(0, id.lastIndexOf(".") );
            String path  = modelName.replace('.', '/');
            String filePath0 = sqlRoot + "/" + dbs.getName() + "/" + path + ".sql";
            String filePath1 = sqlRoot + "/" + dbs.getName() + "/" + path + ".md";
            String filePath2 = sqlRoot + "/" + path + ".sql";
            String filePath3 = sqlRoot + "/" + path + ".md";
            throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "在 "
                    + filePath0+" 和 "
                    + filePath1+" 和 "
                    + filePath2+" 和 "
                    + filePath3+" 和 "+
                    " 未找到[id="+id+"]相关的SQL");
        }
	}

    private boolean readSqlFile(String id,InputStream ins) {
        String modelName = id.substring(0, id.lastIndexOf(".") + 1);
        if(ins == null) return false ;
//			InputStream ins  = null;
//		try{
//			ins = new FileInputStream(file);
//		}catch(IOException ioe){
//			throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "未找到[id="+id+"]相关SQL"+id,ioe);
//		}
        Integer lastModified = ins.hashCode();
//		long lastModified = file.lastModified();
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
                temp = temp.trim();
                lineNum++;
                if (temp.startsWith("===")) {// 读取到===号，说明上一行是key，下面是注释或者SQL语句
                    if (!list.isEmpty() && list.size() > 1) {// 如果链表里面有多个，说明是上一句的sql+下一句的key
                        String tempKey = list.pollLast();// 取出下一句sql的key先存着
                        sql = new StringBuilder();
                        key = list.pollFirst();
                        while (!list.isEmpty()) {// 拼装成一句sql
                            sql.append(list.pollFirst() + lineSeparator);
                        }
                        SQLSource source = new SQLSource(modelName + key,sql.toString().trim());
                        source.setLine(findLineNum);
                        sqlSourceMap.put(modelName + key, source);// 放入map
                        list.addLast(tempKey);// 把下一句的key又放进来
                        findLineNum = lineNum;
                    }
                    boolean sqlStart = false ;
                    String tempNext = null;
                    while((tempNext = bf.readLine()) != null){//处理注释的情况
                        tempNext = tempNext.trim();
                        lineNum++;
                        if (tempNext.startsWith("*")) {//读到注释行，不做任何处理
                            continue;
                        }else if(!sqlStart&&tempNext.trim().length()==0){
                        	//注释的空格
                           continue;
                        }else{
                        	 sqlStart = true;
                        	 list.addLast(tempNext);//===下面不是*号的情况，是一条sql
                             break;//读到一句sql就跳出循环
                        }
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
            SQLSource source = new SQLSource(modelName + key,sql.toString().trim());
            source.setLine(findLineNum);
            sqlSourceMap.put(modelName + key,source);
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
	 * md文件需放在classpath下
	 * @param id
	 * @return
	 * @throws UnexpectedException 
	 */
	private InputStream getRootFile(String id){
		String modelName = id.substring(0, id.lastIndexOf(".") );
		String path  = modelName.replace('.', '/');
        String filePath0 = sqlRoot + "/" + path + ".sql";
		String filePath1 = sqlRoot + "/" + path + ".md";

		InputStream is = this.getFile(filePath0, id);
        if(is==null){
			is = this.getFile(filePath1, id);
            if(is==null){
                return null;
//                throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "在 "+filePath0+" 和 "+filePath1+" 未找到[id="+id+"]相关的SQL");
            }
        }
		return is;
	}

    private InputStream getDBRootFile(String id){
        String modelName = id.substring(0, id.lastIndexOf(".") );
        String path  = modelName.replace('.', '/');
        String filePath0 = sqlRoot + "/" + dbs.getName() + "/" + path + ".sql";
		String filePath1 = sqlRoot + "/" + dbs.getName() + "/" + path + ".md";
        InputStream is = this.getFile(filePath0, id);
        if(is==null){
            is = this.getFile(filePath1, id);
            if(is==null){
                return null;
//                throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_SQL, "在 "+filePath0+" 和 "+filePath1+" 未找到[id="+id+"]相关的SQL");
            }
        }
        return is;
    }

	private InputStream getFile(String filePath, String id){
		InputStream is = this.getClass().getResourceAsStream(filePath);
		return is;
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
		return sqlSourceMap.get(id);
	}
	public DBStyle getDbs() {
		return dbs;
	}
	public void setDbs(DBStyle dbs) {
		this.dbs = dbs;
	}
	
}

