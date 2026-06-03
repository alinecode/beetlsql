package org.beetl.sql.core;

import org.beetl.sql.annotation.entity.TargetSQLManager;
import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.AutoSQLEnum;
import org.beetl.sql.clazz.kit.BeanKit;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.engine.template.SQLErrorInfo;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.engine.template.TemplateContext;
import org.beetl.sql.core.loader.SQLLoader;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.meta.SchemaMetadataManager;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 根据sqlManager操作的pojo的class定义来决定使用哪个SQLManager
 * 比如，物联网应用，时序数据可以入mysql库，随着数据增多，可以考虑到用时nosql，而不需要更改任何代码
 *
 *
 * @author xiandafu
 * @see  TargetSQLManager
 */
public class ConditionalSQLManager extends  SQLManager {



    SQLManager defaultSQLManager;
    Map<String,SQLManager> sqlManagerMap = new HashMap<>();
    Conditional conditional = new DefaultConditional();
    public  interface  Conditional{
        SQLManager decide(Class pojo,SQLManager defaultSQLManager,Map<String,SQLManager> sqlManagerMap);
    }

    public static class DefaultConditional  implements  Conditional{

        @Override
        public SQLManager decide(Class pojo,SQLManager defaultSQLManager, Map<String, SQLManager> sqlManagerMap) {
            TargetSQLManager an = (TargetSQLManager)pojo.getAnnotation(TargetSQLManager.class);
            if(an==null){
                return defaultSQLManager;
            }
            String sqlManagerName = an.value();
            SQLManager target = sqlManagerMap.get(sqlManagerName);
            if(target==null){
                throw new IllegalArgumentException("未发现目标sqlManager "+sqlManagerName+" from "+sqlManagerMap.keySet());
            }

            return target;
        }
    }

    public ConditionalSQLManager(SQLManager defaultSQLManager, Map<String,SQLManager> sqlManagerMap) {
       super();
       this.defaultSQLManager = defaultSQLManager;
       this.sqlManagerMap = sqlManagerMap;
    }



    /**
     * 子类或者Conditional覆盖，决定使用哪个sqlManager
     * @param pojo
     * @return
     */
    protected SQLManager decide(Class pojo){
        return conditional.decide(pojo,defaultSQLManager,sqlManagerMap);
    }

    protected SQLManager decide(SqlId sqlId){
       return defaultSQLManager;
    }




    @Override
    public <T> Query<T> query(Class<T> clazz) {
        SQLManager sqlManager = decide(clazz);
        return new Query<T>(sqlManager, clazz);
    }

    @Override
    public <T> LambdaQuery<T> lambdaQuery(Class<T> clazz) {
        if (BeanKit.queryLambdasSupport) {
            SQLManager sqlManager = decide(clazz);
            return new LambdaQuery<T>(sqlManager, clazz);
        } else {
            throw new UnsupportedOperationException("需要Java8以上");
        }
    }

    @Override
    public boolean isOffsetStartZero() {
        return   defaultSQLManager.isOffsetStartZero();
    }


    /**
     * 是否是生产模式:生产模式MetadataManager ，不查看sql文件变化,默认是false
     *
     * @return
     */
    @Override
    public boolean isProductMode() {
        return   defaultSQLManager.isProductMode();
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
        return defaultSQLManager.getSQLResult(id,paras);
    }



	/**
     * 内部使用，
     *
     * @param source
     * @param inputParas
     * @return
     */
    @Override
    public SQLResult getSQLResult(SQLSource source, Object inputParas) {
        return defaultSQLManager.getSQLResult(source,inputParas);
    }


	@Override
    public SQLResult getSQLResult(SqlId id, Object paras, TemplateContext ctx) {
        return defaultSQLManager.getSQLResult(id,paras,ctx);
	}

    /**
     * 得到指定sqlId的sqlscript对象
     *
     * @param sqlId
     * @return
     */
    @Override
    public SQLExecutor getScript(SqlId sqlId) {
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
    }

    @Override
    public boolean containSqlId(SqlId sqlId){
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
	}

	@Override
    public SQLErrorInfo validateSqlId(SqlId id){
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
	}


	@Override
    public SQLManager viewType(Class view){
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
    }

    @Override
    public SQLManager resultSetMapper(Class resultSetMapperClass){
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
    }

    @Override
    public SQLManager rowMapper(Class rowMapperClass){
        throw new UnsupportedOperationException(ConditionalSQLManager.class.getName());
    }



    /**
     * 得到增删改查模板
     *
     * @param cls          clz
     * @param autoSQLEnum ConstantEnum
     * @return BaseSQLExecutor
     */
    @Override
    public SQLExecutor getScript(Class<?> cls, AutoSQLEnum autoSQLEnum) {
            SQLManager sqlManager = decide(cls);
            return sqlManager.getScript(cls, autoSQLEnum);

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

    @Override
    public <T> List<T> select(SqlId sqlId, Class<T> clazz, Object paras) {
        SQLManager sqlManager = decide(clazz);
		return sqlManager.select(sqlId,clazz,paras);
    }

    /**
     * 根据sqlId查询目标对象
     * @param sqlId
     * @param clazz
     * @return
     */
    @Override
    public <T> List<T> select(SqlId sqlId, Class<T> clazz) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.select(sqlId, clazz, null);
    }








    @Override
    public <T> PageResult<T> pageQuery(SqlId sqlId, Class<T> clazz, Object paras, PageRequest request){
        SQLManager sqlManager = decide(clazz);
       return sqlManager.pageQuery(sqlId,clazz,paras,request);
    }



    /**
     * 根据主键查询 获取唯一记录，如果纪录不存在，将会抛出异常
     *
     * @param clazz
     * @param pk    主键
     * @return
     */

    @Override
    public <T> T unique(Class<T> clazz, Object pk) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.unique(clazz,pk);

    }


    /* =========模版查询=============== */

    /**
     * @param clazz
     * @param pk
     * @return 如果没有找到，返回null
     */

    @Override
    public <T> T single(Class<T> clazz, Object pk) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.single(clazz,pk);
    }

    /**
     * 一个行级锁实现，类似select * from xx where id = ? for update
     *
     * @param clazz
     * @param pk
     * @return
     */

    @Override
    public <T> T lock(Class<T> clazz, Object pk) {
        SQLManager sqlManager = decide(clazz);
       return sqlManager.lock(clazz,pk);
    }

    /**
     * btsql自动生成查询语句，查询clazz代表的表的所有数据。
     *
     * @param clazz
     * @return
     */
	@Override
    public <T> List<T> all(Class<T> clazz) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.all(clazz);
    }

    @Override
	public <T> List<T> all(Class<T> clazz, Object start, Long pageSize) {
		SQLManager sqlManager = decide(clazz);
		return  sqlManager.all(clazz, start, pageSize);
	}


    /**
     * 查询记录数
     *
     * @param clazz
     * @return
     */

    @Override
    public long allCount(Class<?> clazz) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.allCount(clazz);
    }




    @Override
    public <T> T templateOne(T t) {
        SQLManager sqlManager = decide(t.getClass());
        return sqlManager.templateOne(t);
    }


    @Override
    public <T> List<T> template(T t) {
        SQLManager sqlManager = decide(t.getClass());
        return sqlManager.template(t);
    }




    // ========== 取出单个值 ============== //

    /**
     * 查询总数
     *
     * @param t
     * @return
     */

    @Override
    public <T> long templateCount(T t) {
        return templateCount(t.getClass(), t);
    }



    @Override
    public <T> long templateCount(Class<T> target, Object paras) {
        SQLManager sqlManager = decide(target);
       return sqlManager.templateCount(target,paras);
    }


    /**
     * 将查询结果返回成Long类型
     *
     * @param id
     * @param paras
     * @return
     */

    @Override
    public Long longValue(SqlId id, Object paras) {
        return decide(id).longValue(id,paras);
    }

    /**
     * 将查询结果返回成Integer类型
     *
     * @param id
     * @param paras
     * @return
     */

    @Override
    public Integer intValue(SqlId id, Object paras) {
        return decide(id).selectSingle(id, paras, Integer.class);
    }



    /**
     * 将查询结果返回成BigDecimal类型
     *
     * @param id
     * @param paras
     * @return
     */

    @Override
    public BigDecimal bigDecimalValue(SqlId id, Object paras) {
        return decide(id).selectSingle(id, paras, BigDecimal.class);
    }



    /**
     * 返回查询的第一行数据，如果有未找到，返回null
     *
     * @param sqlId
     * @param paras
     * @param target
     * @return
     */

    @Override
    public <T> T selectSingle(SqlId sqlId, Object paras, Class<T> target) {
        SQLManager sqlManager = decide(target);
        return sqlManager.selectSingle(sqlId,paras,target);
    }



    /**
     * 返回一行数据，如果有多行或者未找到，抛错
     *
     * @param id
     * @param paras
     * @param target
     * @return
     */

    @Override
    public <T> T selectUnique(SqlId id, Object paras, Class<T> target) {
        SQLManager sqlManager = decide(target);
        return sqlManager.selectUnique(id,paras,target);
    }


    public <T> List<T> select(SqlId sqlId, Object paras, Class<T> clazz, long start, long size) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.select(sqlId,paras,clazz,start,size);
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

    @Override
    public int deleteById(Class<?> clazz, Object pkValue) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.deleteById(clazz,pkValue);
    }

    /**
     * 删除对象, 通过对象的主键
     *
     * @param obj 对象,必须包含了主键，实际上根据主键来删除
     * @return
     */

    @Override
    public int deleteObject(Object obj) {
        SQLManager sqlManager = decide(obj.getClass());
        return sqlManager.deleteObject(obj);
    }

    // ============= 插入 =================== //

    /**
     * 通用插入操作
     *
     * @param paras
     * @return
     */

    @Override
    public int insert(Object paras) {
        return this.insert(paras.getClass(), paras);
    }


    /**
     * 通用模板插入
     *
     * @param paras
     * @return
     */

    @Override
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

    @Override
    public int insert(Class clazz, Object paras) {
		SQLManager sqlManager = decide(clazz);
		return sqlManager.insert(clazz, paras);
	}



    /**
     * 模板插入，非空值插入到数据库，并且获取到自增主键的值
     *
     * @param clazz
     * @param paras
     * @return
     */

    @Override
    public int insertTemplate(Class clazz, Object paras) {
		SQLManager sqlManager = decide(clazz);
        return sqlManager.insertTemplate(clazz, paras);
    }



	/**
	 * 是否有此对象
	 * @param clazz
	 * @param pk 主健
	 * @return
	 */

    @Override
    public boolean exist(Class<?> clazz, Object pk){
        SQLManager sqlManager = decide(clazz);
		return sqlManager.exist(clazz,pk);

	}






    /**
     * 批量插入
     *
     * @param clazz
     * @param list
     */

    @Override
    public int[] insertBatch(Class clazz, List<?> list) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.insertBatch(clazz,list);
    }





    /**
     * 插入，并获取主键,主键将通过paras所代表的表名来获取
     *
     * @param sqlId
     * @param paras
     * @return
     */

    @Override
    public int insert(SqlId sqlId, Object paras) {
        SQLManager sqlManager = decide(paras.getClass());
        return sqlManager.insert(sqlId,paras);
    }



    /**
	 *
     * 插入单条，并获取自增主键值，因为此接口并未指定实体对象，因此需要keyName来指明数据库主键列
     * 对于一次插入多条，不支持TODO，需要试一下
     * @param sqlId
     * @param paras
     * @param cols,需要得到数据库自动生成的值
     */

    @Override
    public List<Object[]> insert(SqlId sqlId, Object paras, String[] cols) {
        return decide(sqlId).insert(sqlId,paras,cols);
    }

    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     *  出一条，如果未取到，则插入一条，其他情况按照主键更新
     *
     * @param obj
     * @return 受影响条数
     */

    @Override
    public boolean upsert(Object obj) {
        return this.upsert(obj,false);
    }

    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     * 取出一条，如果未取到，则插入一条，其他情况按照主键更新
     * @param obj
     * @return 如果是插入操作，返回true，如果是更新，返回false
     */

    @Override
    public boolean upsertByTemplate(Object obj) {
        return this.upsert(obj,true);
    }


    /**
     * 先判断是否主键为空，如果为空，则插入，如果不为空，则从数据库
     * 取出一条，如果未取到，则插入一条，其他情况按照主键更新
     * @param obj 待更新/插入的实体对象
     * @param template
     * @return 如果是插入操作，返回true，如果是更新，返回false
     */
    @Override
    protected boolean upsert(Object obj, boolean template) {
        SQLManager sqlManager = decide(obj.getClass());
    	return sqlManager.upsert(obj,template);


    }




    /**
     * 更新一个对象
     *
     * @param obj
     * @return
     */

    @Override
    public int updateById(Object obj) {
        SQLManager sqlManager = decide(obj.getClass());
       return sqlManager.updateById(obj);
    }

    /**
     * 为null的值不参与更新，如果想更新null值，请使用updateById
     *
     * @param obj
     * @return 返回更新的条数
     */

    @Override
    public int updateTemplateById(Object obj) {
        SQLManager sqlManager = decide(obj.getClass());
        return sqlManager.updateTemplateById(obj);
    }

    /**
     * @param c     c对应的表名
     * @param paras 参数，仅仅更新paras里包含的值，paras里必须带有主键的值作为更新条件
     * @return 返回更新的条数
     */

    @Override
    public int updateTemplateById(Class c, Map paras) {
        SQLManager sqlManager = decide(c);
        return sqlManager.updateTemplateById(c,paras);
    }

    /**
     * 按照模板更新
     *
     * @param c
     * @param obj
     * @return
     */

    @Override
    public int updateTemplateById(Class c, Object obj) {
        SQLManager sqlManager = decide(c);
        return sqlManager.updateTemplateById(c,obj);
    }

    /****
     * 批量更新
     *
     * @param list
     *            ,包含pojo（不支持map）
     * @return
     */

    @Override
    public int[] updateByIdBatch(List<?> list) {
        if(list.isEmpty()){
            return new int[0];
        }
        Object obj = list.get(0);
        SQLManager sqlManager = decide(obj.getClass());
        return sqlManager.updateByIdBatch(list);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @param obj
     * @return 返回更新的条数
     */

    @Override
    public int update(SqlId sqlId, Object obj) {
       return decide(sqlId).update(sqlId,obj);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @return 返回更新的条数
     */

    @Override
    public int update(SqlId sqlId) {
        return decide(sqlId).update(sqlId);
    }

    /**
     * 执行sql更新（或者删除语句)
     *
     * @param sqlId
     * @param paras
     * @return 返回更新的条数
     */

    @Override
    public int update(SqlId sqlId, Map<String, Object> paras) {
        return decide(sqlId).update(sqlId,paras);
    }

    /**
     * 对pojo批量更新执行sql更新语句，list包含的对象是作为参数，所有属性参与更新
     *
     * @param sqlId
     * @param list
     * @return 返回更新的条数
     */

    @Override
    public int[] updateBatch(SqlId sqlId, List<?> list) {
        return decide(sqlId).updateBatch(sqlId,list);
    }

    /**
     * 批量模板更新方式，list包含的对象是作为参数，非空属性参与更新
     *
     * @param clz
     * @param list
     * @return
     */

    @Override
    public int[] updateBatchTemplateById(Class clz, List<?> list) {
        SQLManager sqlManager = decide(clz);
        return sqlManager.updateBatchTemplateById(clz,list);
    }


    /**
     * 更新指定表
     *
     * @param clazz
     * @param param 参数
     * @return
     */

    @Override
    public int updateAll(Class<?> clazz, Object param) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.updateAll(clazz,param);

    }



    /**
     * 直接执行语句,sql是模板
     *
     * @param sqlTemplate
     * @param clazz
     * @param paras
     * @return
     */

    @Override
    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.execute(sqlTemplate,clazz,paras);
    }


    @Override
    public TableDesc getTableDesc(String table){
        throw new UnsupportedOperationException(table);
//       return  current.get().getTableDesc(table);
    }


    @Override
    public ClassDesc getClassDesc(Class target){
        SQLManager sqlManager = decide(target);
        return sqlManager.getClassDesc(target);
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
        SQLManager sqlManager = decide(clazz);
        return sqlManager.execute(sqlTemplate,clazz,paras,start,size);
    }



    /**
     * sql 模板分页查询，记得使用page函数
     *
     * @param sqlTemplate select #page(*)# from user where name=#userName# ....
     * @param clazz
     * @param request
     * @return
     */

    @Override
    public <T> PageResult<T> executePageQuery(String sqlTemplate, Class<T> clazz, Object paras, PageRequest<T> request) {
        SQLManager sqlManager = decide(clazz);
        return sqlManager.executePageQuery(sqlTemplate,clazz,paras,request);
    }


    /**
     * 直接执行sql更新，sql是模板
     *
     * @param sqlTemplate
     * @param paras
     * @return
     */
    @Override
    public int executeUpdate(String sqlTemplate, Object paras) {
		return  defaultSQLManager.executeUpdate(sqlTemplate,paras);
    }



    /**
     * 直接执行sql语句查询，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param clazz
     * @param p
     * @return 返回查询结果
     */

    @Override
    public <T> List<T> execute(SQLReady p, Class<T> clazz) {
        SQLManager sqlManager = decide(clazz);
    	 return  sqlManager.execute(p,clazz);
    }

    @Override
    public <T> T executeQueryOne(SQLReady p, Class<T> clazz) {
        SQLManager sqlManager = decide(clazz);
        return  sqlManager.executeQueryOne(p,clazz);
    }


    /**
     * 本地分页查询
     * @param p
     * @param clazz
     * @param pageRequest
     * @param <T>
     * @return
     */
    @Override
    public <T> PageResult<T> execute(SQLReady p, Class<T> clazz, PageRequest<T> pageRequest) {
        SQLManager sqlManager = decide(clazz);
        return  sqlManager.execute(p,clazz,pageRequest);
    }

    /**
     * 直接执行sql语句，用于删除或者更新，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param p
     * @return 返回更新条数
     */

    @Override
    public int executeUpdate(SQLReady p) {
		return defaultSQLManager.executeUpdate(p);
    }

    @Override
    public int[] executeBatchUpdate(SQLBatchReady batch) {
         return defaultSQLManager.executeBatchUpdate(batch);
    }

    /**
     * 自己用Connection执行jdbc，通常用于存储过程调用，或者需要自己完全控制的jdbc
     *
     * @param onConnection
     * @return
     */

    @Override
    public <T> T executeOnConnection(OnConnection<T> onConnection) {

       return  defaultSQLManager.executeOnConnection(onConnection);
    }



    @Override
    public SQLLoader getSqlLoader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSqlLoader(SQLLoader sqlLoader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConnectionSource getDs() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setDs(ConnectionSource ds) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取 NameConversion
     *
     * @return
     */
    @Override
    public NameConversion getNc() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setNc(NameConversion nc) {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到当前sqlmanager的数据库类型
     *
     * @return
     */
    @Override
    public DBStyle getDbStyle() {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到sql模板引擎
     *
     * @return
     */
    @Override
    public SQLTemplateEngine getSqlTemplateEngine() {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到MetaDataManager，用来获取数据库元数据，如表，列，主键等信息
     *
     * @return
     */
    @Override
    public MetadataManager getMetaDataManager() {
        throw new UnsupportedOperationException();
    }


       /**
     * 得到所有的Interceptor
     *
     * @return
     */
    @Override
    public Interceptor[] getInters() {
        throw new UnsupportedOperationException();
    }

    /**
     * 设置Interceptor
     *
     * @param inters
     */
    @Override
    public void setInters(Interceptor[] inters) {
		sqlManagerMap.values().forEach(sqlManager -> {
			sqlManager.setInters(inters);
		});
		this.defaultSQLManager.setInters(inters);;
    }

    /**
     * 设置一种id算法用于注解AssignId("xxx"),这样，对于应用赋值主键，交给beetlsql来处理了
     *
     * @param name
     * @param alorithm
     */
    @Override
    public void addIdAutoGen(String name, IDAutoGen alorithm) {
		sqlManagerMap.values().forEach(sqlManager -> {
			sqlManager.addIdAutoGen(name,alorithm);
		});
		this.defaultSQLManager.addIdAutoGen(name,alorithm);
    }

    /**
     * 根据某种算法自动计算id
     *
     * @param name
     * @param param
     * @return
     */
    @Override
    protected Object getAssignIdByIdAutoGen(String name, String param, String table) {
        throw new UnsupportedOperationException();

    }

    /**
     * 获取特殊的BeanPorcessor
     *
     * @return
     */
    @Override
    public Map<String, BeanProcessor> getProcessors() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setProcessors(Map<String, BeanProcessor> processors) {
        throw new UnsupportedOperationException();
    }

    /**
     * 得到默认的jdbc到bean的处理类
     *
     * @return
     */
    @Override
    public BeanProcessor getDefaultBeanProcessors() {
        return defaultSQLManager.getDefaultBeanProcessors();
    }

    /**
     * 设置默认的jdbc 到 bean的映射处理类，用户可以自己扩展处理最新的类型
     *
     * @param defaultBeanProcessors
     */
    @Override
    public void setDefaultBeanProcessors(BeanProcessor defaultBeanProcessors) {
         throw new UnsupportedOperationException();
    }



    @Override
    public <T> T getMapper(Class<T> mapperInterface) {
		TargetSQLManager targetSQLManager = mapperInterface.getAnnotation(TargetSQLManager.class);
		if(targetSQLManager==null){
			return defaultSQLManager.getMapper(mapperInterface);
		}
		SQLManager sqlManager = sqlManagerMap.get(targetSQLManager.value());
		if(sqlManager==null){
			throw new IllegalArgumentException("不能查找到SQLManager "+targetSQLManager);
		}
        return sqlManager.getMapper(mapperInterface);
    }




    @Override
    public ClassLoaderKit getClassLoaderKit() {
       return this.defaultSQLManager.getClassLoaderKit();
    }

    /**
     * 设置classloder，如果没有，pojo的初始化使用ContextClassLoader或者加载Beetlsql的classLoader
     *
     * @param classLoaderKit
     */
    @Override
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
    @Override
    public void addVirtualTable(String realTable, String virtualTable){
        throw new UnsupportedOperationException();
    }

	@Override
    public void setDbStyle(DBStyle dbStyle) {
        throw new UnsupportedOperationException();

	}

	public void setMetaDataManager(SchemaMetadataManager metaDataManager) {
        throw new UnsupportedOperationException();

	}

	@Override
    public SqlIdFactory getSqlIdFactory() {
        throw new UnsupportedOperationException();
	}

	@Override
    public void setSqlIdFactory(SqlIdFactory sqlIdFactory) {
        throw new UnsupportedOperationException();
	}

	@Override
    public String getCharset() {
        throw new UnsupportedOperationException();
	}

	@Override
    public void setCharset(String charset) {
        throw new UnsupportedOperationException();
	}

	@Override
    public boolean isProduct() {
        throw new UnsupportedOperationException();
	}

	@Override
    public void setProduct(boolean product) {
        throw new UnsupportedOperationException();
	}

	@Override
    public void setSQLTemplateEngine(SQLTemplateEngine sqlTemplateEngine) {
        throw new UnsupportedOperationException();
	}

    public void setConditional(Conditional conditional) {
        this.conditional = conditional;
    }
}
