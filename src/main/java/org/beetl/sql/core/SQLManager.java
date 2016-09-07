package org.beetl.sql.core;

import static org.beetl.sql.core.kit.Constants.DELETE_BY_ID;
import static org.beetl.sql.core.kit.Constants.INSERT;
import static org.beetl.sql.core.kit.Constants.SELECT_ALL;
import static org.beetl.sql.core.kit.Constants.SELECT_BY_ID;
import static org.beetl.sql.core.kit.Constants.SELECT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.Constants.SELECT_COUNT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.Constants.UPDATE_ALL;
import static org.beetl.sql.core.kit.Constants.UPDATE_BY_ID;
import static org.beetl.sql.core.kit.Constants.UPDATE_TEMPLATE_BY_ID;
import static org.beetl.sql.core.kit.Constants.classSQL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.sql.core.db.ClassDesc;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.engine.Beetl;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.core.kit.GenKit;
import org.beetl.sql.core.kit.StringKit;
import org.beetl.sql.core.mapper.DefaultMapperBuilder;
import org.beetl.sql.core.mapper.MapperBuilder;
import org.beetl.sql.core.mapping.handler.ScalarHandler;
import org.beetl.sql.ext.SnowflakeIDAutoGen;
import org.beetl.sql.ext.gen.GenConfig;
import org.beetl.sql.ext.gen.GenFilter;
import org.beetl.sql.ext.gen.SourceGen;

/**
 *  Beetsql 操作入口
 * @author xiandafu
 *
 */
public class SQLManager {
	
	private DBStyle dbStyle;
	private SQLLoader sqlLoader;
	private ConnectionSource ds = null;//数据库连接管理
	private NameConversion nc = null;//名字转换器
	private  MetadataManager metaDataManager;
	Interceptor[] inters = {};
	Beetl beetl = null;
	//数据库默认的shcema，对于单个schema应用，无需指定，但多个shcema，需要指定默认的shcema
	private String defaultSchema = null ;
	
	MapperBuilder mapperBuilder = new DefaultMapperBuilder(this);
	boolean offsetStartZero = false;
	
	Map<String,IDAutoGen> idAutonGenMap = new HashMap<String,IDAutoGen>();
	{
		//添加一个id简单实现
		idAutonGenMap.put("simple", new SnowflakeIDAutoGen() );
	}

	
	/** 创建一个beetlsql需要的sqlmanager
	 * @param dbStyle
	 * @param ds
	 */
	public SQLManager(DBStyle dbStyle, ConnectionSource ds) {
		this(dbStyle,new ClasspathLoader("/sql"),ds);

	}
	
	/** 
	 * @param dbStyle  数据个风格
	 * @param sqlLoader sql加载
	 * @param ds 数据库连接
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds) {
		this(dbStyle, sqlLoader, ds, new DefaultNameConversion(), new Interceptor[]{},null);

	}
	
	/**
	 * @param dbStyle  数据个风格
	 * @param sqlLoader sql加载
	 * @param ds 数据库连接
	 * @param nc  数据库名称与java名称转化规则
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds,NameConversion nc) {
		this(dbStyle, sqlLoader, ds, nc, new Interceptor[]{},null);

	}
	
	
	/**
	 * @param dbStyle
	 * @param sqlLoader
	 * @param ds
	 * @param nc
	 * @param inters
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader,
			ConnectionSource ds, NameConversion nc, Interceptor[] inters) {
		this(dbStyle, sqlLoader, ds, nc, inters,null);
	}
	
	
	/**
	 * 
	 * @param dbStyle
	 * @param sqlLoader
	 * @param ds
	 * @param nc
	 * @param inters
	 * @param defaultSchema 数据库访问的schema，为null自动判断
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader,
			ConnectionSource ds, NameConversion nc, Interceptor[] inters,String defaultSchema) {
		this(dbStyle,sqlLoader,ds,nc,inters,defaultSchema,new Properties());
	}
	
	/**
	 * 
	 * @param dbStyle
	 * @param sqlLoader
	 * @param ds
	 * @param nc
	 * @param inters
	 * @param defaultSchema
	 * @param ps  额外的beetl配置
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader,
			ConnectionSource ds, NameConversion nc, Interceptor[] inters,String defaultSchema,Properties ps) {
		this.defaultSchema = defaultSchema;
		beetl = new Beetl(sqlLoader,ps);
		this.dbStyle = dbStyle;
		this.sqlLoader = sqlLoader;
		
		this.ds = ds;
		this.nc = nc;
		this.inters = inters;
		this.dbStyle.setNameConversion(this.nc);
		
		this.dbStyle.setMetadataManager(initMetadataManager());
		this.dbStyle.init(beetl);
		
		offsetStartZero = Boolean.parseBoolean(beetl.getPs().getProperty("OFFSET_START_ZERO").trim());
	}
	
	/**
	 * 
	 * @MethodName: getMetadataManager   
	 * @Description: 获取MetaDataManager  
	 * @param @return  
	 * @return MetadataManager  
	 * @throws
	 */
	private MetadataManager initMetadataManager(){
		
		if(metaDataManager == null){
			metaDataManager =  new MetadataManager(this.ds,this);
		}
		return metaDataManager;
		
	}
	
	
	/**
	 * 是否是生产模式:生产模式MetadataManager 不缓存table信息，不查看sql文件变化,默认是false
	 * @return
	 */
	public boolean isProductMode(){
		return !sqlLoader.isAutoCheck();
	}
	
	/** 不执行数据库操作，仅仅得到一个sql模板执行后的实际得sql和相应的参数
	 * @param id
	 * @param paras
	 * @return
	 */
	public SQLResult getSQLResult(String id, Map<String, Object> paras) {
		SQLScript script = getScript(id);
		return script.run(paras);
	}
	
	/** 不执行数据库操作，仅仅得到一个sql模板执行后的实际得sql和相应的参数
	 * @param id
	 * @param paras
	 * @return
	 */
	public SQLResult getSQLResult(String id, Object paras) {
		SQLScript script = getScript(id);
		Map map = new HashMap();
		map.put("_root",paras);
		return script.run(map);
	}
	
	public SQLResult getSQLResult(String id, Map<String, Object> paras,String parentId) {
		SQLScript script = getScript(id);
		return script.run(paras,parentId);
	}

	public SQLScript getScript(String id) {
		SQLSource source  = sqlLoader.getSQL(id);
		SQLScript script = new SQLScript(source, this);	
		return script;
	}

	/**
	 * 生成增删改查模板
	 * @param cls
	 * @param tempId
	 * @return
	 */
	public SQLScript getScript(Class<?> cls, int tempId) {
		String className = cls.getSimpleName().toLowerCase();
		String id = className +"."+ classSQL[tempId];
		
		SQLSource tempSource = this.sqlLoader.getGenSQL(id);
		if (tempSource != null) {
			return new SQLScript(tempSource,this);
		}
		switch (tempId) {
			case SELECT_BY_ID: {			
				tempSource = this.dbStyle.genSelectById(cls);
				break ;
			}
			case SELECT_BY_TEMPLATE: {
				tempSource = this.dbStyle.genSelectByTemplate(cls);
				break ;
			}
			case SELECT_COUNT_BY_TEMPLATE: {
				tempSource = this.dbStyle.genSelectCountByTemplate(cls);
				break ;
			}
			case DELETE_BY_ID: {
				tempSource = this.dbStyle.genDeleteById(cls);
				break ;
			}
			case SELECT_ALL: {
				tempSource = this.dbStyle.genSelectAll(cls);
				break ;
			}
			case UPDATE_ALL: {
				tempSource = this.dbStyle.genUpdateAll(cls);
				break ;
			}
			case UPDATE_BY_ID: {
				tempSource = this.dbStyle.genUpdateById(cls);
				break ;
			}
			
			case UPDATE_TEMPLATE_BY_ID: {
				tempSource = this.dbStyle.genUpdateTemplate(cls);
				break ;
			}
			
			case INSERT: {
				tempSource = this.dbStyle.genInsert(cls);
				break ;
			}
			default: {
				throw new UnsupportedOperationException();
			}
		}
		
		tempSource.setId(id);
		sqlLoader.addGenSQL(id, tempSource);
		return new SQLScript(tempSource,this);
	}
	
	/****
	 * 获取为分页语句
	 * @param selectId
	 * @return
	 */
	public SQLScript getPageSqlScript(String selectId) {
		String pageId = selectId+"_page";
		SQLSource source  = sqlLoader.getGenSQL(pageId);
		if(source!=null){
			return  new SQLScript(source, this);
		}
		SQLSource script = sqlLoader.getGenSQL(selectId);
		if(script==null){
			script = sqlLoader.getSQL(selectId);
		}

		String template = script.getTemplate();
		String pageTemplate = dbStyle.getPageSQL(template);
		source = new SQLSource(pageId,pageTemplate);
		sqlLoader.addGenSQL(pageId, source);
		return new SQLScript(source, this);
	}
	

	/*============ 查询部分 ==================*/
	
	
	
	/**
	 * 通过sqlId进行查询,查询结果映射到clazz上
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras 参数集合
	 * @return Pojo集合
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras) { 
		return this.select(sqlId, clazz, paras, null);
	}
	
	/**
	 * 通过sqlId进行查询,查询结果映射到clazz上，mapper类可以定制映射
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras 参数集合
	 * @param mapper 自定义结果映射方式
	 * @return
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras,RowMapper<T> mapper) { 
		SQLScript script = getScript(sqlId);
		return script.select(clazz, paras,mapper);
	}
	
	/**
	 * 通过sqlId进行查询，查询结果映射到clazz上，输入条件是个Bean，
	 * Bean的属性可以被sql语句引用，如bean中有name属性,即方法getName,则sql语句可以包含
	 * name属性，如select * from xxx where name = #name#
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras Bean
	 * @return Pojo集合
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras) { 
		return this.select(sqlId, clazz, paras, null);
	}
	
	/**
	 * 通过sqlId进行查询:查询结果映射到clazz上，输入条件是个Bean,
	 * Bean的属性可以被sql语句引用，如bean中有name属性,即方法getName,则sql语句可以包含name属性，
	 * 如select * from xxx where name = #name#。mapper类可以指定结果映射方式
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras Bean
	 * @param mapper 自定义结果映射方式
	 * @return
	 */

	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, RowMapper<T> mapper) { 
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("_root",paras);
		SQLScript script = getScript(sqlId);
		return script.select(clazz, param,mapper);
	}
	
	/** 翻页查询
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras Bean
	 * @param start 开始位置
	 * @param size  查询条数
	 * @return
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, long start, long size) { 
		return this.select(sqlId, clazz, paras, null, start, size);
	}
	
	/**
	 * 翻页查询
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras Bean
	 * @param mapper 自定义结果映射方式
	 * @param start 开始位置
	 * @param size  查询条数
	 * @return Pojo集合
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, RowMapper<T> mapper, long start, long size) { 
		SQLScript script = getScript(sqlId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return script.select(map, clazz, mapper,start, size);
	}
	
	/**
	 * 翻页查询
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras 条件集合
	 * @param start 开始位置
	 * @param size  查询条数
	 * @return
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, long start, long size) { 
		
		SQLScript script = getScript(sqlId);
		return script.select(paras, clazz, null,start, size);
	}
	
	/**
	 * 翻页查询
	 * @param sqlId sql标记
	 * @param clazz 需要映射的Pojo类
	 * @param paras 条件集合
	 * @param mapper 自定义结果映射方式
	 * @param start 开始位置
	 * @param size  查询条数
	 * @return
	 */
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, RowMapper<T> mapper, long start, long size) { 
		SQLScript script = getScript(sqlId);
		return script.select(paras, clazz, mapper,start, size);
	}
	
	public <T> void pageQuery(String sqlId,Class<T> clazz,PageQuery query){
		pageQuery(sqlId, clazz, query, null);
	}
	
	/** 翻页查询，假设有sqlId和sqlId$count 俩个sql存在，beetlsql会通过
	 * 这俩个sql来查询总数以及翻页操作，如果没有sqlId$count，则假设sqlId
	 * 包含了page函数或者标签 ，如<p></p>
	 * <pre>
	 * queryUser		
	 * ===
	 * select #page("a.*,b.name")# from user a left join role b ....
	 * </pre>
	 * 
	 * 
	 * 
	 * @param sqlId
	 * @param query
	 */
	public <T> void pageQuery(String sqlId,Class<T> clazz,PageQuery query,RowMapper<T> mapper){
		Object paras = query.getParas();
		Map<String,Object> root =  null;
		Long totalRow = query.getTotalRow();
		List<T> list = null;
		if(paras==null){
			root = new HashMap<String,Object>();
		}else if(paras instanceof Map){
			root = (Map<String,Object>)paras;
		}else{
			root = new HashMap<String,Object>();
			root.put("_root", paras);
		}
	
		if(query.getOrderBy()!=null){
			root.put(DBStyle.ORDER_BY, query.getOrderBy());
		}
		
		String sqlCountId = sqlId.concat("$count");
		boolean hasCountSQL = this.sqlLoader.exist(sqlCountId);
		if(query.getTotalRow()==-1){
			//需要查询行数
			if(hasCountSQL){
				totalRow = this.selectSingle(sqlCountId, root, Long.class);
			}else{
				root.put(PageQuery.pageFlag, PageQuery.pageObj);
				//todo: 如果sql并不包含翻页标签，没有报错，会有隐患
				totalRow = this.selectSingle(sqlId, root, Long.class);
			}
			
			if(totalRow==null){
				totalRow = 0l;
			}
			query.setTotalRow(totalRow);
		}
		
		
		if(!hasCountSQL)root.remove(PageQuery.pageFlag);
		
		if(totalRow!=0){
			long start=(this.offsetStartZero?0:1)+(query.getPageNumber()-1)*query.getPageSize();
			long size = query.getPageSize();
			list = this.select(sqlId,clazz, root,mapper,start,size);
		}else{
			list = Collections.EMPTY_LIST;
		}
		
		query.setList(list);
		
		
	}
	
	
	
	/**
	 * 根据主键查询
	 * 获取唯一记录，如果纪录不存在，将会抛出异常
	 * @param clazz
	 * @param pk 主键 
	 * @return
	 */
	public <T> T unique(Class<T> clazz,Object pk) {
		SQLScript script = getScript(clazz, SELECT_BY_ID);
		return script.unique(clazz, null,pk);
	}
	
	/**
	 * 根据主键查询,获取唯一记录，如果纪录不存在，将会抛出异常
	 * @param clazz
	 * @param mapper 自定义结果映射方式
	 * @param pk 主键
	 * @return
	 */
	public <T> T unique(Class<T> clazz, RowMapper<T> mapper,Object pk) {
		SQLScript script = getScript(clazz, SELECT_BY_ID);
		return script.unique(clazz, mapper,pk);
	}
	
	/**
	 * 
	 * @param clazz
	 * @param pk
	 * @return 如果没有找到，返回null
	 */
	public <T> T single(Class<T> clazz,Object pk) {
		SQLScript script = getScript(clazz, SELECT_BY_ID);
		return script.single(clazz, null,pk);
	}
	
	
	/*=========模版查询===============*/
	
	/**
	 * btsql自动生成查询语句，查询clazz代表的表的所有数据。
	 * @param clazz
	 * @return
	 */
	public <T> List<T> all(Class<T> clazz) {
		SQLScript script = getScript(clazz, SELECT_ALL);
		return script.select(clazz, null);
	}
	
	/**
	 * btsql自动生成查询语句，查询clazz代表的表的所有数据。
	 * @param clazz
	 * @param start
	 * @param size
	 * @return
	 */
	public <T> List<T> all(Class<T> clazz, long start, long size) {
		SQLScript script = getScript(clazz, SELECT_ALL);
		return script.select(null, clazz, null, start, size);
	}
	
	
	/**
	 * 查询记录数
	 * @param clazz
	 * @return
	 */
	public long allCount(Class<?> clazz) {
		SQLScript script = getScript(clazz, SELECT_COUNT_BY_TEMPLATE);
		return script.selectSingle(null, Long.class);
	}
	
	/**
	 * 查询所有记录
	 * @param clazz
	 * @param mapper
	 * @param start
	 * @param end
	 * @return
	 */
	public <T> List<T> all(Class<T> clazz, RowMapper<T> mapper, long start, int end) {
		SQLScript script = getScript(clazz, SELECT_ALL);
		return script.select(null, clazz, mapper, start, end);
	}
	
	/**
	 * 查询所有记录
	 * @param clazz
	 * @param mapper
	 * @return
	 */
	public <T> List<T> all(Class<T> clazz,RowMapper<T> mapper) {
		SQLScript script = getScript(clazz, SELECT_ALL);
		return script.select(clazz, null,mapper);
	}
	
	
	public <T> List<T> template(T t) {
		SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("_root",t);
		return (List<T>) script.select(t.getClass(), param,null);
	}
	
	public <T> List<T> template(T t,RowMapper mapper) {
		SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("_root",t);
		return (List<T>) script.select(t.getClass(), param,mapper);
	}
	
	public <T> List<T> template(T t,long start,long size) {
		return this.template(t, null, start, size);
	}
	
	public <T> List<T> template(T t,RowMapper mapper,long start,long size) {		
		SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
		SQLScript pageScript = this.getPageSqlScript(script.id);
		Map<String, Object> param = new HashMap<String, Object>();
		this.dbStyle.initPagePara(param, start, size);
		param.put("_root",t);
		
		return (List<T>) pageScript.select(t.getClass(), param,mapper);
	}
	

	/**
	 * 查询总数
	 * @param t
	 * @return
	 */
	public <T> long templateCount(T t) {
		SQLScript script = getScript(t.getClass(), SELECT_COUNT_BY_TEMPLATE);
		Long l = script.singleSelect(t, Long.class);
		return l;
	}
	
	
	//========== 取出单个值  ============== //
	
	public Long  longValue(String id,Map<String, Object> paras) {
		return this.selectSingle(id, paras, Long.class);
	}
	
	public Long  longValue(String id,Object paras) {
		return this.selectSingle(id, paras, Long.class);
	}
	
	public Integer  intValue(String id,Object paras) {
		return this.selectSingle(id, paras, Integer.class);
	}
	
	public Integer  intValue(String id,Map<String, Object> paras) {
		return this.selectSingle(id, paras, Integer.class);
	}
	
	public BigDecimal  bigDecimalValue(String id,Object paras) {
		return this.selectSingle(id, paras, BigDecimal.class);
	}
	
	public BigDecimal  bigDecimalValue(String id,Map<String, Object> paras) {
		return this.selectSingle(id, paras, BigDecimal.class);
	}
	
	public <T> T selectSingle(String id,Object paras, Class<T> target) {
		SQLScript script = getScript(id);
		return script.singleSelect(paras, target);
	}
	
	public <T> T selectSingle(String id,Map<String, Object> paras, Class<T> target) {
		SQLScript script = getScript(id);
		return script.selectSingle(paras, target);
	}
	
	public <T> T selectUnique(String id,Object paras, Class<T> target) {
		SQLScript script = getScript(id);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("_root", paras);
		return script.selectUnique(map, target);
	}
	
	public <T> T selectUnique(String id,Map<String, Object> paras, Class<T> target) {
		SQLScript script = getScript(id);
		return script.selectUnique(paras, target);
	}
	
	
	/**
	 * 
	 * delete from user where 1=1 and id= #id#
	 * 
	 * 根据Id删除数据：支持联合主键
	 * @param clazz
	 * @param pkValue
	 * @return
	 */
	public int deleteById(Class<?> clazz, Object pkValue) {
		
		SQLScript script = getScript(clazz, DELETE_BY_ID);
		return script.deleteById(clazz, pkValue);
	}
	
	
	//============= 插入 ===================  //
	
	public int  insert(Class<?> clazz,Object paras){
		
		return this.insert(clazz,paras,false);
	}
	
	public int  insert(Object paras){
		return this.insert(paras.getClass(),paras, false);
	}
	
	public int  insert(Object paras,boolean autoAssignKey){
		return this.insert(paras.getClass(),paras, autoAssignKey);
	}
	
	public int  insert(Class clazz,Object paras,boolean autoAssignKey){
		if(autoAssignKey){
			KeyHolder holder = new KeyHolder();
			Class target = clazz;
			int result = this.insert(target, paras, holder);
			String table = this.nc.getTableName(target);
			ClassDesc desc = this.metaDataManager.getTable(table).getClassDesc(target, nc);
			
			if(desc.getIdCols().isEmpty()){
				return result;
			}else{
				Method getterMethod = (Method) desc.getIdMethods().get(desc.getIdCols().get(0));
				
				String name = getterMethod.getName();
				String setterName = name.replaceFirst("get", "set");
				try{
					Method setterMethod = target.getMethod(setterName, new Class[]{getterMethod.getReturnType()});
					Object value = holder.getKey();
					value = ScalarHandler.convertValueToRequiredType(value, getterMethod.getReturnType());
					setterMethod.invoke(paras, new Object[]{value});
					return result;
				}catch(Exception ex){
					
					throw new UnsupportedOperationException("autoAssignKey failure "+ex.getMessage());
				}
			}
			
		
			
		}else{
			SQLScript script = getScript(clazz,INSERT );
			return script.insert(paras);
		}
		
	}
	
	/**
	 * 批量插入
	 * @param clazz
	 * @param list
	 */
	public void insertBatch(Class clazz,List<?> list){
		SQLScript script = getScript(clazz,INSERT );
		script.updateBatch(list);
	}
	
	
	
	
	
	/** 插入，并获取主键
	 * @param clazz
	 * @param paras
	 * @param holder
	 */
	public int  insert(Class<?> clazz,Object paras,KeyHolder holder){
		SQLScript script = getScript(clazz,INSERT);
		return script.insert(paras,holder );
	}
	
	
	
	/** 插入，并获取主键
	 * @param sqlId
	 * @param paras
	 * @param holder
	 * @param keyName  主键列名称
	 */
	public int  insert(String sqlId,Object paras,KeyHolder holder,String keyName){
		SQLScript script = getScript(sqlId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		return script.insertBySqlId(map, holder,keyName);
	}
	
	/**
	 * 插入，并获取主键,主键将通过paras所代表的表名来获取
	 * 
	 * @param sqlId
	 * @param paras
	 * @param holder
	 * @return
	 */
	public int  insert(String sqlId,Object paras,KeyHolder holder){
		SQLScript script = getScript(sqlId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("_root", paras);
		if(holder!=null){
			String tableName = this.nc.getTableName(paras.getClass());
			TableDesc  table = this.metaDataManager.getTable(tableName);
			Set<String> idCols = table.getIdNames();
			if(idCols.size()!=1){
				throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR,"有多个主键，不能自动设置");
			}
			return script.insertBySqlId(map, holder,((CaseInsensitiveOrderSet)idCols).getFirst());
		}else{
			return script.insertBySqlId(map,null,null);
		}
		
	}
	
	/**
	 * 
	 * @param sqlId
	 * @param clazz
	 * @param paras
	 * @param holder
	 * @return
	 */
	public int  insert(String sqlId,Class<?> clazz,Map paras,KeyHolder holder){
		SQLScript script = getScript(sqlId);
		if(holder!=null){
			String tableName = this.nc.getTableName(clazz);
			TableDesc  table = this.metaDataManager.getTable(tableName);
			ClassDesc  clsDesc = table.getClassDesc(this.nc);
			Set<String> idCols = table.getIdNames();
			if(idCols.size()!=1){
				throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR,"有多个主键，不能自动设置");
			}
			return script.insertBySqlId(paras, holder,((CaseInsensitiveOrderSet)idCols).getFirst());
		}else{
			return script.insertBySqlId(paras, holder,null);

		}
	}
	
	
	
	/** 插入，并获取主键
	 * @param sqlId
	 * @param paras
	 * @param holder
	 * @param keyName  主键列名称
	 */
	public int  insert(String sqlId,Map paras,KeyHolder holder,String keyName){
		SQLScript script = getScript(sqlId);
		return script.insertBySqlId(paras, holder,keyName);
	}
	
	
	/**
	 * 
	 * 需要处理","的问题，可能会出现
		
	 * 
	 * @param obj
	 * @return
	 */
	public int updateById(Object obj){
		SQLScript script = getScript(obj.getClass(), UPDATE_BY_ID);
		return script.update(obj);
	}
	
	/**
	 * 为null的值不参与更新，如果想更新null值，请使用updateById
	 * @param obj
	 * @return
	 */
	public int updateTemplateById(Object obj){
		SQLScript script = getScript(obj.getClass(), UPDATE_TEMPLATE_BY_ID);
		return script.update(obj);
	}
	/**
	 * 
	 * @param c   c对应的表名
	 * @param paras 参数，如需要更新的值，还有id
	 * @return
	 */
	public int updateTemplateById(Class c ,Map paras){
		SQLScript script = getScript(c, UPDATE_TEMPLATE_BY_ID);
		return script.update(paras);
	}
	
	/****
	 * 批量更新
	 * @param list ,包含pojo（不支持map）
	 * @return
	 */
	public int[] updateByIdBatch(List<?> list){
		if(list == null || list.isEmpty()){
			return new int[0];
		}
		SQLScript script = getScript(list.get(0).getClass(), UPDATE_BY_ID);
		return script.updateBatch(list);
	}
	
	/**  执行sql更新语句
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public int update(String sqlId, Object obj){
		SQLScript script = getScript(sqlId);
		return script.update(obj);
	}
	
	/**  执行sql更新语句
	 * @param sqlId
	 * @param paras
	 * @return
	 */
	public int update(String sqlId, Map<String, Object> paras){
		SQLScript script = getScript(sqlId);
		return script.update(paras);
	}
	
	/**  对pojo批量更新执行sql更新语句
	 * @param sqlId 
	 * @param list 
	 * @return
	 */
	public int[] updateBatch(String sqlId,List<?> list){
		SQLScript script = getScript(sqlId);
		return script.updateBatch(list);
	}
	
	/**批量更新
	 * @param sqlId
	 * @param maps  参数放在map里
	 * @return
	 */
	public int[] updateBatch(String sqlId,Map<String, Object>[] maps){
		SQLScript script = getScript(sqlId);
		return script.updateBatch(maps);
	}
	

	/** 更新指定表
	 * @param clazz
	 * @param param 参数
	 * @return
	 */
	public int updateAll(Class<?> clazz, Object param){
		
		SQLScript script = getScript(clazz, UPDATE_ALL);
		return script.update(param);
	}
	
	/** 只使用master执行:
	 * <pre>
	 *    sqlManager.useMaster(new DBRunner(){
	 *    		public void run(SQLManager sqlManager){
	 *          	sqlManager.select .....  
	 *          }
	 *    )
	 * </pre>
	 * @param f
	 */
	public void useMaster(DBRunner f){
		f.start(this,true);
	}
	
	/** 只使用Slave执行:
	 * <pre>
	 *    sqlManager.useSlave(new DBRunner(){
	 *    		public void run(SQLManager sqlManager){
	 *          	sqlManager.select .....  
	 *          }
	 *    )
	 * </pre>
	 * @param f
	 */
	public void useSlave(DBRunner f){
		f.start(this,false);
	}
	
	
	
	/** 直接执行语句,sql是模板
	 * @param sqlTemplate
	 * @param clazz
	 * @param paras
	 * @return
	 */
	public <T> List<T> execute(String sqlTemplate,Class<T> clazz, Object paras){

		Map map = new HashMap();
		map.put("_root", paras);
		return this.execute(sqlTemplate, clazz, map);
	}
	
	/** 直接执行sql语句，sql是模板
	 * @param sqlTemplate
	 * @param clazz
	 * @param paras
	 * @return
	 */
	public <T> List<T> execute(String sqlTemplate,Class<T> clazz, Map paras){
		String key ="auto._gen_" +sqlTemplate;
		SQLSource source = sqlLoader.getGenSQL(key);
		if(source==null){
			source = new SQLSource(key,sqlTemplate);
			this.sqlLoader.addGenSQL(key, source);		
		}
	
		SQLScript script = new SQLScript(source,this);
		return script.select(clazz, paras);
	}
	
	/**
	 * 直接执行sql模版语句，sql是模板
	 * @param sqlTemplate
	 * @param clazz
	 * @param paras
	 * @param start 
	 * @param size
	 * @return
	 */
	public <T> List<T> execute(String sqlTemplate,Class<T> clazz, Map paras,long start,long size){
		String key ="auto._gen_" +sqlTemplate;
		SQLSource source = sqlLoader.getGenSQL(key);
		if(source==null){
			String pageSql =  this.dbStyle.getPageSQL(sqlTemplate);
			source = new SQLSource(key,pageSql);
			this.sqlLoader.addGenSQL(key, source);		
		}
		
		this.dbStyle.initPagePara(paras, start, size);
		SQLScript script = new SQLScript(source,this);
		return script.select(clazz, paras);
	}
	
	public <T> List<T> execute(String sqlTemplate,Class<T> clazz, Object paras,long start,long size){
		
		Map map = new HashMap();
		map.put("_root", paras);
		return this.execute(sqlTemplate, clazz, map, start, size);
	}
	
	
	/** 直接执行sql更新，sql是模板
	 * @param sqlTemplate
	 * @param paras
	 * @return
	 */
	public int  executeUpdate(String sqlTemplate,Object paras){
		String key ="auto._gen_" +sqlTemplate;
		SQLSource source = sqlLoader.getGenSQL(key);
		if(source==null){
			source = new SQLSource(key,sqlTemplate);
			this.sqlLoader.addGenSQL(key, source);		
		}
	
		SQLScript script = new SQLScript(source,this);
		Map map = new HashMap();
		map.put("_root", paras);
		return script.update(map);
	}
	
	/** 直接更新sql，sql是模板
	 * @param sqlTemplate
	 * @param paras
	 * @return
	 */
	public int  executeUpdate(String sqlTemplate,Map paras){
		String key ="auto._gen_" +sqlTemplate;
		SQLSource source = sqlLoader.getGenSQL(key);
		if(source==null){
			source = new SQLSource(key,sqlTemplate);
			this.sqlLoader.addGenSQL(key, source);		
		}
		SQLScript script = new SQLScript(source,this);
		return script.update(paras);
	}
	/**
	 * 直接执行sql语句，sql语句已经是准备好的，采用preparedstatment执行
	 * @param clazz
	 * @param p
	 * @return 返回查询结果
	 */
	public <T> List<T> execute(SQLReady p,Class<T> clazz){
		SQLSource source = new SQLSource(p.getSql(),p.getSql());
		SQLScript script = new SQLScript(source,this);
		return script.sqlReadySelect(clazz, p);
	}
	
	/** 直接执行sql语句，sql语句已经是准备好的，采用preparedstatment执行
	 * @param p
	 * 
	 * @return 返回更新条数
	 */
	public int executeUpdate(SQLReady p){
		SQLSource source = new SQLSource(p.getSql(),p.getSql());
		SQLScript script = new SQLScript(source,this);
		return script.sqlReadyExecuteUpdate( p);
	}
	
	public <T> T executeOnConnection(OnConnection<T> onConnection){
		Connection conn = null;
		try {
			conn = onConnection.getConn(getDs());
			return onConnection.call(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
		} finally {
			//非事务环境提交
			if (!getDs().isTransaction()) {
				try {
						if (!conn.getAutoCommit()){
							conn.commit();
						}
						conn.close();
					
				} catch (SQLException e) {
					throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
				}

			}
		}
	}
	
	
	//========= 代码生成 =============//
	
	/** 根据表名生成对应的pojo类
	 * @param table 表名
	 * @param pkg 包名,如 com.test
	 * @param srcPath: 文件保存路径
	 * @param config 配置生成的风格
	 * @throws Exception
	 */
	public void genPojoCode(String table,String pkg,String srcPath,GenConfig config) throws Exception{
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,config);
		gen.gen();
	}
	
	/** 同上，但路径自动根据项目当前目录推测，是src目录下，或者src/main/java 下
	 * @param table
	 * @param pkg
	 * @param config
	 * @throws Exception
	 */
	public void genPojoCode(String table,String pkg,GenConfig config) throws Exception{
		String srcPath = GenKit.getJavaSRCPath();	
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,config );
		gen.gen();
	}
	
	/** 生成pojo类,默认路径是当前工程src目录,或者是src/main/java 下
	 * @param table
	 * @param pkg
	 * @throws Exception
	 */
	public void genPojoCode(String table,String pkg) throws Exception{
		String srcPath = GenKit.getJavaSRCPath();	
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,new GenConfig() );
		gen.gen();
	}
	
	/** 仅仅打印pojo类到控制台
	 * @param table
	 * @throws Exception
	 */
	public void genPojoCodeToConsole(String table) throws Exception{
		String pkg =SourceGen.defaultPkg;
		String srcPath= System.getProperty("user.dir");
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,new GenConfig().setDisplay(true));
		gen.gen();
	}
	
	/** 仅仅打印pojo类到控制台
	 * @param table
	 * @throws Exception
	 */
	public void genPojoCodeToConsole(String table,GenConfig config) throws Exception{
		String pkg =SourceGen.defaultPkg;
		String srcPath= System.getProperty("user.dir");
		config.setDisplay(true);
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,config);
		gen.gen();
	}
	
	/** 将sql模板文件输出到src下，如果采用的是ClasspathLoader，则使用ClasspathLoader的配置，否则，生成到src的sql代码里
	 * @param table
	 */
	public void genSQLFile(String table) throws Exception{
		String path = "/sql";
		if(this.sqlLoader instanceof ClasspathLoader){
			path = ((ClasspathLoader)sqlLoader).sqlRoot;
		}
		String fileName =StringKit.toLowerCaseFirstOne(this.nc.getClassName(table));
		String target = GenKit.getJavaResourcePath()+"/"+path+"/"+fileName+".md";
		FileWriter writer = new FileWriter(new File(target));
		genSQLTemplate(table,writer);
		writer.close();
		System.out.println("gen \""+table +"\" success at "+target);
	}
	
	
	
	/** 生成sql语句片段,包含了条件查询，列名列表，更新，插入等语句
	 * @param table
	 */
	public void genSQLTemplateToConsole(String table ) throws Exception{
	
		genSQLTemplate(table,new OutputStreamWriter( System.out));
		
	}
	
	private void genSQLTemplate(String table,Writer w ) throws IOException{
		String template = null;
		Configuration cf =beetl.getGroupTemplate().getConf();
		
		String hs  = cf.getPlaceholderStart();
		String he = cf.getPlaceholderEnd();
		StringBuilder cols = new StringBuilder();
		String sql = "select "+hs+"use(\"cols\")"+he+ " from "+table+" where "+hs+"use(\"condition\")"+he;
		cols.append("sample").append("\n===\n").append("* 注释").append("\n\n\t").append( sql);
		cols.append("\n");
		
		
		cols.append("\ncols").append("\n===\n").append("").append("\n\t").append( this.dbStyle.genColumnList(table));
		cols.append("\n");
		
		cols.append("\nupdateSample").append("\n===\n").append("").append("\n\t").append( this.dbStyle.genColAssignPropertyAbsolute(table));
		cols.append("\n");
		String condition = this.dbStyle.genCondition(table);
		condition = condition.replaceAll("\\n", "\n\t");
		cols.append("\ncondition").append("\n===\n").append("").append("\n\t").append(condition );
		cols.append("\n");
		w.write(cols.toString());
		w.flush();
	}
	/**
	 * 
	 * @param pkg
	 * @param config
	 */
	public void genALL(String pkg,GenConfig config,GenFilter filter) throws Exception{
		Set<String> tables = this.metaDataManager.allTable();
		
		for(String table:tables){
			table = metaDataManager.getTable(table).getName();
			if(filter==null||filter.accept(table)){
				try {
					//生成代码
					this.genPojoCode(table, pkg, config);
					//生成模板文件
					this.genSQLFile(table);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					continue;
				}
			}
		}
	}
	
	public <T> T getMapper(Class<T> mapperInterface){
		return this.mapperBuilder.getMapper(mapperInterface);
	}
	
	//===============get/set===============

	public SQLLoader getSqlLoader() {
		return sqlLoader;
	}

	public void setSqlLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
	}

	
	public ConnectionSource getDs() {
		return ds;
	}

	public void setDs(ConnectionSource ds) {
		this.ds = ds;
	}
	
	public NameConversion getNc() {
		return nc;
	}

	public void setNc(NameConversion nc) {
		this.nc = nc;
		this.dbStyle.setNameConversion(nc);
	}

	public DBStyle getDbStyle() {
		return dbStyle;
	}

	public Beetl getBeetl() {
		return beetl;
	}

	public  MetadataManager getMetaDataManager() {
		return metaDataManager;
	}

	

	public String getDefaultSchema() {
		
		return defaultSchema;
	}

	public void setDefaultSchema(String defaultSchema) {
		this.defaultSchema = defaultSchema;
	}

	public MapperBuilder getMapperBuilder() {
		return mapperBuilder;
	}

	public void setMapperBuilder(MapperBuilder mapperBuilder) {
		this.mapperBuilder = mapperBuilder;
	}

	public Interceptor[] getInters() {
		return inters;
	}

	public void setInters(Interceptor[] inters) {
		this.inters = inters;
	}
	
	
	public void addIdAutonGen(String name,IDAutoGen alorithm){
		this.idAutonGenMap.put(name, alorithm);
	}
	
	/** 根据某种算法自动计算id
	 * @param name
	 * @param param
	 * @return
	 */
	protected Object getAssignIdByIdAutonGen(String name,String param,String table){
		IDAutoGen idGen=  idAutonGenMap.get(name);
		if(idGen==null){
			throw new BeetlSQLException(BeetlSQLException.ID_AUTOGEN_ERROR,"未发现自动id生成器:"+name+" in "+table);
		}
		return idGen.nextID(param);
		
	}
	


}
