package org.beetl.sql.core;

import static org.beetl.sql.core.kit.Constants.DELETE_BY_ID;
import static org.beetl.sql.core.kit.Constants.INSERT;
import static org.beetl.sql.core.kit.Constants.SELECT_ALL;
import static org.beetl.sql.core.kit.Constants.SELECT_BY_ID;
import static org.beetl.sql.core.kit.Constants.SELECT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.Constants.SELECT_COUNT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.Constants.UPDATE_ALL;
import static org.beetl.sql.core.kit.Constants.UPDATE_BY_ID;
import static org.beetl.sql.core.kit.Constants.classSQL;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.KeyHolder;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.engine.Beetl;
import org.beetl.sql.ext.gen.GenConfig;
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
	
	/** 
	 * @param dbStyle  数据个风格
	 * @param sqlLoader sql加载
	 * @param ds 数据库连接
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds) {
		this(dbStyle, sqlLoader, ds, new HumpNameConversion(), new Interceptor[]{});

	}
	
	/**
	 * @param dbStyle  数据个风格
	 * @param sqlLoader sql加载
	 * @param ds 数据库连接
	 * @param nc  数据库名称与java名称转化规则
	 */
	public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds,NameConversion nc) {
		this(dbStyle, sqlLoader, ds, nc, new Interceptor[]{});

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
		beetl = new Beetl(sqlLoader);
		this.dbStyle = dbStyle;
		this.sqlLoader = sqlLoader;
		this.ds = ds;
		this.nc = nc;
		this.inters = inters;
		this.dbStyle.setNameConversion(this.nc);
		this.dbStyle.setMetadataManager(initMetadataManager());
		this.dbStyle.init(beetl);
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
	
	public SQLResult getSQLResult(String id, Map<String, Object> paras) {
		SQLScript script = getScript(id);
		return script.run(paras);
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
	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, int start, int size) { 
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
	public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, RowMapper<T> mapper, int start, int size) { 
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
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, int start, int size) { 
		
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
	public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, RowMapper<T> mapper, int start, int size) { 
		SQLScript script = getScript(sqlId);
		return script.select(paras, clazz, mapper,start, size);
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
	 * 根据主键查询
	 * @param clazz
	 * @param mapper 自定义结果映射方式
	 * @param pk 主键
	 * @return
	 */
	public <T> T unique(Class<T> clazz, RowMapper<T> mapper,Object pk) {
		SQLScript script = getScript(clazz, SELECT_BY_ID);
		return script.unique(clazz, mapper,pk);
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
	public <T> List<T> all(Class<T> clazz, int start, int size) {
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
		return script.singleSelect(null, Long.class);
	}
	
	/**
	 * 查询所有记录
	 * @param clazz
	 * @param mapper
	 * @param start
	 * @param end
	 * @return
	 */
	public <T> List<T> all(Class<T> clazz, RowMapper<T> mapper, int start, int end) {
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
	
	public <T> List<T> template(T t,int start,int size) {
		return this.template(t, null, start, size);
	}
	
	public <T> List<T> template(T t,RowMapper mapper,int start,int size) {		
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
		return script.singleSelect(paras, target);
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
		SQLScript script = getScript(clazz,INSERT );
		return script.insert(paras);
	}
	
	public int  insert(Object paras){
		SQLScript script = getScript(paras.getClass(),INSERT );
		return script.insert(paras);
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
	
	/****
	 * 批量更新
	 * @param list ,包含pojo（不支持map）
	 * @return
	 */
	public int[] updateByIdBatch(List<?> list){
		if(list == null || list.isEmpty()){
			return null;
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
		String key ="auto._gen_" +sqlTemplate;
		SQLSource source = sqlLoader.getGenSQL(key);
		if(source==null){
			source = new SQLSource(key,sqlTemplate);
			this.sqlLoader.addGenSQL(key, source);		
		}
	
		SQLScript script = new SQLScript(source,this);
		return script.select(clazz, paras);
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
		String srcPath = null;
		String userDir = System.getProperty("user.dir");
		if(userDir==null){
			throw new NullPointerException("用户目录未找到");
		}
		File src = new File(userDir,"src");
		File javaSrc = new File(src.toString(),"/main/java");
		if(javaSrc.exists()){
			srcPath = javaSrc.toString();
		}else{
			srcPath = src.toString();
		}		
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,config );
		gen.gen();
	}
	
	/** 生成pojo类,默认路径是当前工程src目录,或者是src/main/java 下
	 * @param table
	 * @param pkg
	 * @throws Exception
	 */
	public void genPojoCode(String table,String pkg) throws Exception{
		String srcPath = null;
		String userDir = System.getProperty("user.dir");
		if(userDir==null){
			throw new NullPointerException("用户目录未找到");
		}
		File src = new File(userDir,"src");
		File javaSrc = new File(src.toString(),"/main/java");
		if(javaSrc.exists()){
			srcPath = javaSrc.toString();
		}else{
			srcPath = src.toString();
		}		
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,new GenConfig() );
		gen.gen();
	}
	
	/** 仅仅打印pojo类到控制台
	 * @param table
	 * @throws Exception
	 */
	public void genPojoCodeToConsole(String table) throws Exception{
		String pkg ="com.test";
		String srcPath= System.getProperty("user.dir");
		SourceGen gen = new SourceGen(this,table,pkg,srcPath,new GenConfig().setDisplay(true));
		gen.gen();
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



}
