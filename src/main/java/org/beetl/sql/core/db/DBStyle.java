package org.beetl.sql.core.db;

import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.engine.Beetl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * 用来描述数据库差异，主键生成，sql语句，翻页等
 *
 * @author xiandafu
 */
public interface DBStyle {

    int ID_ASSIGN = 1;
    int ID_AUTO = 2;
    int ID_SEQ = 3;

    String OFFSET = "_pageOffset";
    String PAGE_SIZE = "_pageSize";
    String PAGE_END = "_pageEnd";
    String ORDER_BY = "_orderBy";


    int DB_MYSQL = 1;
    int DB_ORACLE = 2;
    int DB_POSTGRES = 3;
    int DB_SQLSERVER = 4;
    int DB_SQLLITE = 5;
    int DB_DB2 = 6;
    int DB_H2 = 7;


    void init(Beetl beetl);

    SQLSource genSelectById(Class<?> cls);
    
    SQLSource genSelectByIdForUpdate(Class<?> cls);

    SQLSource genSelectByTemplate(Class<?> cls);

    SQLSource genSelectCountByTemplate(Class<?> cls);

    SQLSource genDeleteById(Class<?> cls);

    SQLSource genSelectAll(Class<?> cls);

    SQLSource genUpdateAll(Class<?> cls);

    SQLSource genUpdateById(Class<?> cls);

    SQLSource genUpdateAbsolute(Class<?> cls);

    SQLSource genUpdateTemplate(Class<?> cls);

    SQLSource genInsert(Class<?> cls);

    SQLSource genInsertTemplate(Class<?> cls);

    //代码片段生成方法
    String genColumnList(String table);

    String genCondition(String table);

    String genColAssignProperty(String table);

    String genColAssignPropertyAbsolute(String table);

    Set<String> getCols(String table);

    String getName();

    int getDBType();

    /**
     * 把正常sql转换成分页sql, 参数是 ?
     * <pre>
     * 正常sql: select * from tb_bee
     *
     * 假设 offset = 2
     * 假设 pageSize  = 9
     * (mysql示例) 出来的sql:
     * select * from tb_bee limit 2 , 9
     * </pre>
     *
     * @param sql      正常查询sql
     * @param offset   offset
     * @param pageSize pageSize
     * @return 分页sql
     */
    String getPageSQLStatement(String sql, long offset, long pageSize);

    String getPageSQL(String sql);

    void initPagePara(Map<String, Object> paras, long start, long size);

    int getIdType(Class c,String idProperty);

    KeyWordHandler getKeyWordHandler();

    void setKeyWordHandler(KeyWordHandler keyWordHandler);


    NameConversion getNameConversion();

    void setNameConversion(NameConversion nameConversion);

    MetadataManager getMetadataManager();

    void setMetadataManager(MetadataManager metadataManager);

}
