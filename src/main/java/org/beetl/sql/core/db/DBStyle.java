package org.beetl.sql.core.db;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.engine.Beetl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * 用来描述数据库差异，主键生成，sql语句，翻页等
 * @author xiandafu
 *
 */
public interface DBStyle {

	public static final int ID_ASSIGN = 1 ;
	public static final int ID_AUTO = 2 ;
	public static final int ID_SEQ = 3 ;
	
	public final static String OFFSET = "_pageOffset";
	public final static String PAGE_SIZE = "_pageSize";
	public final static String PAGE_END = "_pageEnd";
	public final static String ORDER_BY = "_orderBy";
	
	
	public final static   int DB_MYSQL = 1;
	public final static   int DB_ORACLE = 2;
	public final static   int DB_POSTGRES = 3;
	public final static   int DB_SQLSERVER = 4;
	public final static   int DB_SQLLITE = 5;
	public final static   int DB_DB2 = 6;
	public final static   int DB_H2 = 7;
	
	

	public void init(Beetl beetl);
	
	public SQLSource genSelectById(Class<?> cls);
	public SQLSource genSelectByTemplate(Class<?> cls);
	public SQLSource genSelectCountByTemplate(Class<?> cls);
	public SQLSource genDeleteById(Class<?> cls);
	public SQLSource genSelectAll(Class<?> cls);
	public SQLSource genUpdateAll(Class<?> cls);
	public SQLSource genUpdateById(Class<?> cls);
	public SQLSource genUpdateTemplate(Class<?> cls);
	public SQLSource genInsert(Class<?> cls);
	public SQLSource genInsertTemplate(Class<?> cls);
    //代码片段生成方法
    public String genColumnList(String table);
    public String genCondition(String table);
    public String genColAssignProperty(String table);
    public String genColAssignPropertyAbsolute(String table);
    public Set<String> getCols(String  table);
    
    
	public String getName();
	public int getDBType();
	
	public String getPageSQL(String sql);
	public void initPagePara(Map<String, Object> paras,long start,long size);
	
	public int getIdType(Method idMethod);
	
	public KeyWordHandler getKeyWordHandler();
	public void setKeyWordHandler(KeyWordHandler keyWordHandler);
	
	
	public NameConversion getNameConversion();
	public MetadataManager getMetadataManager();
	public void setNameConversion(NameConversion nameConversion);
	public void setMetadataManager(MetadataManager metadataManager);
	
}
