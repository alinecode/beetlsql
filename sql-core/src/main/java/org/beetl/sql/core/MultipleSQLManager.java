package org.beetl.sql.core;

import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.meta.SchemaMetadataManager;
import org.beetl.sql.core.engine.template.SQLErrorInfo;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.engine.template.TemplateContext;
import org.beetl.sql.core.loader.SQLLoader;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;

import java.math.BigDecimal;
import java.util.*;


/**
 * Beetsql 操作入口
 *
 * @author xiandafu
 */
public class MultipleSQLManager extends  SQLManager {



    ThreadLocal<SQLManager> current = new ThreadLocal<>();

    protected MultipleSQLManager() {
       super();
    }


    public <T> Query<T> query(Class<T> clazz) {
        return new Query<T>(this, clazz);
    }

    public <T> LambdaQuery<T> lambdaQuery(Class<T> clazz) {
        if (BeanKit.queryLambdasSupport) {
            return new LambdaQuery<T>(this, clazz);
        } else {
            throw new UnsupportedOperationException("需要Java8以上");
        }
    }

    @Override
    public boolean isOffsetStartZero() {
        return current.get().isOffsetStartZero();
    }


    /**
     * 是否是生产模式:生产模式MetadataManager ，不查看sql文件变化,默认是false
     *
     * @return
     */
    @Override
    public boolean isProductMode() {
        return  current.get().isOffsetStartZero();
    }

    /**
     * 不执行数据库操作，仅仅得到一个sql模板执行后的实际得sql和相应的参数
     *
     * @param id
     * @param paras
     * @return
     */
    @Override
    public SQLResult getSQLResult(SqlId id, Object paras) {
        return current.get().getSQLResult(id,paras);
    }



	/**
     * 内部使用，
     *
     * @param source
     * @param inputParas
     * @return
     */
    public SQLResult getSQLResult(SQLSource source, Object inputParas) {
       return current.get().getSQLResult(source,inputParas);
    }


	public SQLResult getSQLResult(SqlId id, Object paras, TemplateContext ctx) {
        return current.get().getSQLResult(id,paras,ctx);
	}

    /**
     * 得到指定sqlId的sqlscript对象
     *
     * @param sqlId
     * @return
     */
    public SQLExecutor getScript(SqlId sqlId) {
       return current.get().getScript(sqlId);
    }

    public boolean containSqlId(SqlId sqlId){
        return current.get().containSqlId(sqlId);
	}

	public SQLErrorInfo validateSqlId(SqlId id){
		return current.get().validateSqlId(id);
	}


	public SQLManager viewType(Class view){
        current.get().viewType(view);
        return this;
    }

    public SQLManager resultSetMapper(Class resultSetMapperClass){
        current.get().resultSetMapper(resultSetMapperClass);
        return this;
    }

    public SQLManager rowMapper(Class rowMapperClass){
        current.get().rowMapper(rowMapperClass);
        return this;
    }



    /**
     * 得到增删改查模板
     *
     * @param cls          clz
     * @param autoSQLEnum ConstantEnum
     * @return BaseSQLExecutor
     */
    public SQLExecutor getScript(Class<?> cls, AutoSQLEnum autoSQLEnum) {
            return current.get().getScript(cls, autoSQLEnum);

    }

    /* ============ 查询部分 ================== */



    /**
     * 通过sqlId进行查询，查询结果映射到clazz上，输入条件是个Bean，
     * Bean的属性可以被sql语句引用，如bean中有name属性,即方法getName,则sql语句可以包含 name属性，如select *
     * from xxx where name = #name#
     *
     * @param sqlId sql标记
     * @param clazz 需要映射的Pojo类
     * @param paras Bean
     * @return Pojo集合
     */

    public <T> List<T> select(SqlId sqlId, Class<T> clazz, Object paras) {
		return current.get().select(sqlId,clazz,paras);
    }

    /**
     * 根据sqlId查询目标对象
     * @param sqlId
     * @param clazz
     * @return
     */
    public <T> List<T> select(SqlId sqlId, Class<T> clazz) {
        return this.select(sqlId, clazz, null);
    }








    public <T> PageResult<T> pageQuery(SqlId sqlId, Class<T> clazz, Object paras, PageRequest request){

       return current.get().pageQuery(sqlId,clazz,paras,request);
    }



    /**
     * 根据主键查询 获取唯一记录，如果纪录不存在，将会抛出异常
     *
     * @param clazz
     * @param pk    主键
     * @return
     */

    public <T> T unique(Class<T> clazz, Object pk) {
        return current.get().unique(clazz,pk);

    }


    /* =========模版查询=============== */

    /**
     * @param clazz
     * @param pk
     * @return 如果没有找到，返回null
     */

    public <T> T single(Class<T> clazz, Object pk) {
        return current.get().single(clazz,pk);
    }

    /**
     * 一个行级锁实现，类似select * from xx where id = ? for update
     *
     * @param clazz
     * @param pk
     * @return
     */

    public <T> T lock(Class<T> clazz, Object pk) {
       return current.get().lock(clazz,pk);
    }

    /**
     * btsql自动生成查询语句，查询clazz代表的表的所有数据。
     *
     * @param clazz
     * @return
     */

    public <T> List<T> all(Class<T> clazz) {
        return current.get().all(clazz);
    }




    /**
     * 查询记录数
     *
     * @param clazz
     * @return
     */

    public long allCount(Class<?> clazz) {
        return current.get().allCount(clazz);
    }




    public <T> T templateOne(T t) {
        return current.get().templateOne(t);
    }


    public <T> List<T> template(T t) {
        return current.get().template(t);
    }



    // ========== 取出单个值 ============== //

    /**
     * 查询总数
     *
     * @param t
     * @return
     */

    public <T> long templateCount(T t) {
        return templateCount(t.getClass(), t);
    }



    public <T> long templateCount(Class<T> target, Object paras) {
       return current.get().templateCount(target,paras);
    }


    /**
     * 将查询结果返回成Long类型
     *
     * @param id
     * @param paras
     * @return
     */

    public Long longValue(SqlId id, Object paras) {
        return current.get().longValue(id,paras);
    }

    /**
     * 将查询结果返回成Integer类型
     *
     * @param id
     * @param paras
     * @return
     */

    public Integer intValue(SqlId id, Object paras) {
        return this.selectSingle(id, paras, Integer.class);
    }



    /**
     * 将查询结果返回成BigDecimal类型
     *
     * @param id
     * @param paras
     * @return
     */

    public BigDecimal bigDecimalValue(SqlId id, Object paras) {
        return this.selectSingle(id, paras, BigDecimal.class);
    }



    /**
     * 返回查询的第一行数据，如果有未找到，返回null
     *
     * @param sqlId
     * @param paras
     * @param target
     * @return
     */

    public <T> T selectSingle(SqlId sqlId, Object paras, Class<T> target) {
        return current.get().selectSingle(sqlId,paras,target);
    }



    /**
     * 返回一行数据，如果有多行或者未找到，抛错
     *
     * @param id
     * @param paras
     * @param target
     * @return
     */

    public <T> T selectUnique(SqlId id, Object paras, Class<T> target) {
        return current.get().selectUnique(id,paras,target);
    }


    public <T> List<T> select(SqlId sqlId, Object paras, Class<T> clazz, long start, long size) {
        return current.get().select(sqlId,paras,clazz,start,size);
    }


    /**
     * delete from user where 1=1 and id= #id#
     * <p>
     * 根据Id删除数据：支持联合主键
     *
     * @param clazz
     * @param pkValue
     * @return
     */

    public int deleteById(Class<?> clazz, Object pkValue) {
        return current.get().deleteById(clazz,pkValue);
    }

    /**
     * 删除对象, 通过对象的主键
     *
     * @param obj 对象,必须包含了主键，实际上根据主键来删除
     * @return
     */

    public int deleteObject(Object obj) {
        return current.get().deleteObject(obj);
    }

    // ============= 插入 =================== //

    /**
     * 通用插入操作
     *
     * @param paras
     * @return
     */

    public int insert(Object paras) {
        return this.insert(paras.getClass(), paras);
    }


    /**
     * 通用模板插入
     *
     * @param paras
     * @return
     */

    public int insertTemplate(Object paras) {
        return this.insertTemplate(paras.getClass(), paras);
    }



    /**
     * 对于有自增主键的表，插入一行记录
     *
     * @param clazz
     * @param paras
     * @return
     */

    public int insert(Class clazz, Object paras) {
		return generalInsert(clazz, paras, false);
	}



    /**
     * 模板插入，非空值插入到数据库，并且获取到自增主键的值
     *
     * @param clazz
     * @param paras
     * @return
     */

    public int insertTemplate(Class clazz, Object paras) {
        return generalInsert(clazz, paras, true);
    }



	/**
	 * 是否有此对象
	 * @param clazz
	 * @param pk 主健
	 * @return
	 */

    public boolean exist(Class<?> clazz, Object pk){
		return current.get().exist(clazz,pk);

	}

    protected int generalInsert(Class clazz, Object paras, boolean template) {
        return current.get().generalInsert(clazz,paras,template);
    }




    /**
     * 批量插入
     *
     * @param clazz
     * @param list
     */

    public int[] insertBatch(Class clazz, List<?> list) {
        return current.get().insertBatch(clazz,list);
    }





    /**
     * 插入，并获取主键,主键将通过paras所代表的表名来获取
     *
     * @param sqlId
     * @param paras
     * @return
     */

    public int insert(SqlId sqlId, Object paras) {
        return current.get().insert(sqlId,paras);
    }



    /**
	 *
     * 插入单条，并获取自增主键值，因为此接口并未指定实体对象，因此需要keyName来指明数据库主键列
     * 对于一次插入多条，不支持TODO，需要试一下
     * @param sqlId
     * @param paras
     * @param cols,需要得到数据库自动生成的值
     */

    public Object[] insert(SqlId sqlId, Object paras, String[] cols) {
        return current.get().insert(sqlId,paras,cols);
    }

    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     *  出一条，如果未取到，则插入一条，其他情况按照主键更新
     *
     * @param obj
     * @return 受影响条数
     */

    public boolean upsert(Object obj) {
        return this.upsert(obj,false);
    }

    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     * 取出一条，如果未取到，则插入一条，其他情况按照主键更新
     * @param obj
     * @return 受影响条数
     */

    public boolean upsertByTemplate(Object obj) {
        return this.upsert(obj,true);
    }


    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     * 取出一条，如果未取到，则插入一条，其他情况按照主键更新
     * @param obj 待更新/插入的实体对象
     * @param template
     * @return 受影响条数
     */
    protected boolean upsert(Object obj,boolean template) {
    	return current.get().upsert(obj,template);

    }




    /**
     * 更新一个对象
     *
     * @param obj
     * @return
     */

    public int updateById(Object obj) {
       return current.get().updateById(obj);
    }

    /**
     * 为null的值不参与更新，如果想更新null值，请使用updateById
     *
     * @param obj
     * @return 返回更新的条数
     */

    public int updateTemplateById(Object obj) {
        return current.get().updateTemplateById(obj);
    }

    /**
     * @param c     c对应的表名
     * @param paras 参数，仅仅更新paras里包含的值，paras里必须带有主键的值作为更新条件
     * @return 返回更新的条数
     */

    public int updateTemplateById(Class c, Map paras) {
        return current.get().updateTemplateById(c,paras);
    }

    /**
     * 按照模板更新
     *
     * @param c
     * @param obj
     * @return
     */

    public int updateTemplateById(Class c, Object obj) {
        return current.get().updateTemplateById(c,obj);
    }

    /****
     * 批量更新
     *
     * @param list
     *            ,包含pojo（不支持map）
     * @return
     */

    public int[] updateByIdBatch(List<?> list) {
        return current.get().updateByIdBatch(list);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @param obj
     * @return 返回更新的条数
     */

    public int update(SqlId sqlId, Object obj) {
       return current.get().update(sqlId,obj);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @return 返回更新的条数
     */

    public int update(SqlId sqlId) {
        return current.get().update(sqlId);
    }

    /**
     * 执行sql更新（或者删除语句)
     *
     * @param sqlId
     * @param paras
     * @return 返回更新的条数
     */

    public int update(SqlId sqlId, Map<String, Object> paras) {
        return current.get().update(sqlId,paras);
    }

    /**
     * 对pojo批量更新执行sql更新语句，list包含的对象是作为参数，所有属性参与更新
     *
     * @param sqlId
     * @param list
     * @return 返回更新的条数
     */

    public int[] updateBatch(SqlId sqlId, List<?> list) {
        return current.get().updateBatch(sqlId,list);
    }

    /**
     * 批量模板更新方式，list包含的对象是作为参数，非空属性参与更新
     *
     * @param clz
     * @param list
     * @return
     */

    public int[] updateBatchTemplateById(Class clz, List<?> list) {
        return current.get().updateBatchTemplateById(clz,list);
    }


    /**
     * 更新指定表
     *
     * @param clazz
     * @param param 参数
     * @return
     */

    public int updateAll(Class<?> clazz, Object param) {
        return current.get().updateAll(clazz,param);

    }



    /**
     * 直接执行语句,sql是模板
     *
     * @param sqlTemplate
     * @param clazz
     * @param paras
     * @return
     */

    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras) {

        return current.get().execute(sqlTemplate,clazz,paras);
    }


    public TableDesc getTableDesc(String table){
       return  current.get().getTableDesc(table);
    }


    public ClassDesc getClassDesc(Class target){
        return current.get().getClassDesc(target);
    }

    /**
     * 直接执行sql查询语句，sql是模板
     *
     * @param sqlTemplate
     * @param clazz
     * @param paras
     * @return
     */

    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Map paras) {
       return current.get().execute(sqlTemplate,clazz,paras);
    }

    /**
     * 直接执行sql模版语句，sql是模板
     *
     * @param sqlTemplate
     * @param clazz
     * @param paras
     * @param start
     * @param size
     * @return
     */

    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras, long start, long size) {
       return current.get().execute(sqlTemplate,clazz,paras,start,size);
    }



    /**
     * sql 模板分页查询，记得使用page函数
     *
     * @param sqlTemplate select #page(*)# from user where name=#userName# ....
     * @param clazz
     * @param request
     * @return
     */

    public <T> PageResult<T> executePageQuery(String sqlTemplate, Class<T> clazz, Object paras,PageRequest<T> request) {
		return current.get().executePageQuery(sqlTemplate,clazz,paras,request);
    }


    /**
     * 直接执行sql更新，sql是模板
     *
     * @param sqlTemplate
     * @param paras
     * @return
     */
    public int executeUpdate(String sqlTemplate, Object paras) {
		return  current.get().executeUpdate(sqlTemplate,paras);
    }



    /**
     * 直接执行sql语句查询，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param clazz
     * @param p
     * @return 返回查询结果
     */

    public <T> List<T> execute(SQLReady p, Class<T> clazz) {
    	 return  current.get().execute(p,clazz);
    }


    /**
     * 本地分页查询
     * @param p
     * @param clazz
     * @param pageRequest
     * @param <T>
     * @return
     */
    public <T> PageResult<T> execute(SQLReady p, Class<T> clazz, PageRequest<T> pageRequest) {
        return  current.get().execute(p,clazz,pageRequest);
    }

    /**
     * 直接执行sql语句，用于删除或者更新，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param p
     * @return 返回更新条数
     */

    public int executeUpdate(SQLReady p) {
		return current.get().executeUpdate(p);
    }
    

    public int[] executeBatchUpdate(SQLBatchReady batch) {
        return  current.get().executeBatchUpdate(batch);
    }




    /**
     * 自己用Connection执行jdbc，通常用于存储过程调用，或者需要自己完全控制的jdbc
     *
     * @param onConnection
     * @return
     */

    public <T> T executeOnConnection(OnConnection<T> onConnection) {

        return current.get().executeOnConnection(onConnection);
    }



    public SQLLoader getSqlLoader() {
        return current.get().getSqlLoader();
    }

    public void setSqlLoader(SQLLoader sqlLoader) {
        throw new UnsupportedOperationException();
    }

    public ConnectionSource getDs() {
        return current.get().getDs();
    }


    public void setDs(ConnectionSource ds) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取 NameConversion
     *
     * @return
     */
    public NameConversion getNc() {
        return current.get().getNc();
    }


    public void setNc(NameConversion nc) {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到当前sqlmanager的数据库类型
     *
     * @return
     */
    public DBStyle getDbStyle() {
        return current.get().getDbStyle();
    }

    /**
     * 得到sql模板引擎
     *
     * @return
     */
    public SQLTemplateEngine getSqlTemplateEngine() {
        return current.get().getSqlTemplateEngine();
    }

    /**
     * 得到MetaDataManager，用来获取数据库元数据，如表，列，主键等信息
     *
     * @return
     */
    public MetadataManager getMetaDataManager() {
        return current.get().getMetaDataManager();
    }


       /**
     * 得到所有的Interceptor
     *
     * @return
     */
    public Interceptor[] getInters() {
        return current.get().getInters();
    }

    /**
     * 设置Interceptor
     *
     * @param inters
     */
    public void setInters(Interceptor[] inters) {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置一种id算法用于注解AssignId("xxx"),这样，对于应用赋值主键，交给beetlsql来处理了
     *
     * @param name
     * @param alorithm
     */
    public void addIdAutonGen(String name, IDAutoGen alorithm) {
        throw new UnsupportedOperationException();
    }

    /**
     * 根据某种算法自动计算id
     *
     * @param name
     * @param param
     * @return
     */
    protected Object getAssignIdByIdAutonGen(String name, String param, String table) {
       return current.get().getAssignIdByIdAutonGen(name,param,table);

    }

    /**
     * 获取特殊的BeanPorcessor
     *
     * @return
     */
    public Map<String, BeanProcessor> getProcessors() {
        return current.get().getProcessors();
    }


    public void setProcessors(Map<String, BeanProcessor> processors) {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到默认的jdbc到bean的处理类
     *
     * @return
     */
    public BeanProcessor getDefaultBeanProcessors() {
        return current.get().getDefaultBeanProcessors();
    }

    /**
     * 设置默认的jdbc 到 bean的映射处理类，用户可以自己扩展处理最新的类型
     *
     * @param defaultBeanProcessors
     */
    public void setDefaultBeanProcessors(BeanProcessor defaultBeanProcessors) {
         throw new UnsupportedOperationException();
    }



    public <T> T getMapper(Class<T> mapperInterface) {
       return current.get().getMapper(mapperInterface);
    }




    public ClassLoaderKit getClassLoaderKit() {
       return current.get().getClassLoaderKit();
    }

    /**
     * 设置classloder，如果没有，pojo的初始化使用ContextClassLoader或者加载Beetlsql的classLoader
     *
     * @param classLoaderKit
     */
    public void setClassLoaderKit(ClassLoaderKit classLoaderKit) {
        throw new UnsupportedOperationException();
    }

    /**
     * 为不存在的表设置一个数据库真正的表，以用于获取metadata
	 * 主要用于数据库分库分表
	 *
	 *
     * @param virtualTable
     * @param realTable
     */
    public void addVirtualTable(String realTable,String virtualTable){
        throw new UnsupportedOperationException();
    }

	public void setDbStyle(DBStyle dbStyle) {
        throw new UnsupportedOperationException();

	}

	public void setMetaDataManager(SchemaMetadataManager metaDataManager) {
        throw new UnsupportedOperationException();

	}

	public SqlIdFactory getSqlIdFactory() {
		return current.get().getSqlIdFactory();
	}

	public void setSqlIdFactory(SqlIdFactory sqlIdFactory) {
        throw new UnsupportedOperationException();
	}

	public String getCharset() {
		return current.get().getCharset();
	}

	public void setCharset(String charset) {
        throw new UnsupportedOperationException();
	}

	public boolean isProduct() {
		return current.get().isProduct();
	}

	public void setProduct(boolean product) {
        throw new UnsupportedOperationException();
	}

	public void setSQLTemplateEngine(SQLTemplateEngine sqlTemplateEngine) {
        throw new UnsupportedOperationException();
	}


	public SQLManager use(String name){
        SQLManager sqlManager = group.get(name);
        if(sqlManager==null){
            throw new IllegalArgumentException(name);
        }
        current.set(sqlManager);
        return sqlManager;

    }
}
