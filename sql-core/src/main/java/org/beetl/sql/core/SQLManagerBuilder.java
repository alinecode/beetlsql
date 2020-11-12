package org.beetl.sql.core;

import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.kit.ClassLoaderKit;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.meta.MetadataManager;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.core.engine.template.SQLTemplateEngine;
import org.beetl.sql.core.loader.AbstractClassPathSQLLoader;
import org.beetl.sql.core.loader.MarkdownClasspathLoader;
import org.beetl.sql.core.loader.SQLLoader;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.ext.DebugInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SQLManager 构建器: 为了简化SQLManager的构建过程
 * <pre>
 * 使用:
 * ConnectionSource ds = ConnectionSourceHelper.getSimple(driver, url, userName, password);
 * SQLManagerBuilder smBuilder = SQLManager.newBuilder(ds);
 * SQLManager sm = smBuilder.build();
 * </pre>
 * <p>
 * 如果在构建器中没有进行任何配置, 那么使用的默认值是:
 * <ul>
 * <li>属性名 : 默认值</li>
 * <li>dbStyle : new MySqlStyle();</li>
 * <li>sqlLoader : new MarkdownClasspathLoader()</li>
 * <li>nc : new DefaultNameConversion()</li>
 * <li>inters : new Interceptor[]{}</li>
 * <li>defaultSchema : null</li>
 * <li>beetlPs : new Properties()</li>
 * </ul>
 * <p>
 * <BR>
 * create time : 2017-04-28 14:44
 *
 * @author luoyizhu@gmail.com,xiandafu@126.com
 */
public class SQLManagerBuilder {

    /** 额外的beetl配置 */
    Properties beetlPs;

    /** 拦截器 */
    Interceptor[] inters;

    /** 数据库默认的shcema，对于单个schema应用，无需指定，但多个shcema，需要指定默认的shcema */
    private String defaultSchema;

    /** 数据库默认的atalog */
    private String defaultCatalog;

    private boolean setSchema = false;
	/** 默认的sqlManager名称**/
    private String name=null;
    /** 数据库风格 */
    private DBStyle dbStyle;

    /** sql加载 */
    private SQLLoader sqlLoader;

    /** 名字转换器 */
    private NameConversion nc;

    /** 数据库连接管理,包含主从，或者其他逻辑，比如多租户 */
    private ConnectionSource ds;

    /*
    * 数据库元数据
    */
	private MetadataManager metadataManager;

	private SqlIdFactory sqlIdFactory;

    private String charset="UTF-8";

    private boolean isProduct = false;

    SQLTemplateEngine sqlTemplateEngine;


    BeanProcessor beanProcessor;


    /** 拦截器 */
    private List<Interceptor> interceptorList = new LinkedList<Interceptor>();


    private ClassLoaderKit classLoaderKit = null;

	SQLManagerNameGenerator sqlManagerNameGenerator = new SQLManagerNameGenerator();
    public static Map<String,SQLManager> sqlManagerMap = new ConcurrentHashMap<>();

    public SQLManagerBuilder(ConnectionSource ds) {
        this.ds = ds;
    }

    /**
     * @return 构建 SQLManager
     */
    public SQLManager build() {
        check();

		SQLManager mySqlManager =  new SQLManager();

		mySqlManager.isProduct = isProduct;
		mySqlManager.charset = charset;

        DBStyle myDbStyle = this.getDbStyle();
        SQLLoader  mySqlLoader = this.getSqlLoader();
        NameConversion myNc = this.getNc();
        Interceptor[] myInters = this.getInters();
        //BeetlSQL配置，在btsql.properties,btsql-ext.properties扩展
        Properties myPs = this.getBeetlPs();

        SQLTemplateEngine mysSqlTemplateEngine = this.getSqlTemplateEngine();

		//设置dbStyle
        myDbStyle.setNameConversion(myNc);
        myDbStyle.init(mysSqlTemplateEngine,myPs);

        MetadataManager myMetadataManager = dbStyle.initMetadataManager(ds);
        ClassLoaderKit myClassLoaderKit = this.getClassLoaderKit();

		//设置sqlloader
        mySqlLoader.setDbStyle(myDbStyle);
        if(mySqlLoader instanceof AbstractClassPathSQLLoader){
            ((AbstractClassPathSQLLoader)mySqlLoader).setClassLoaderKit(myClassLoaderKit);
        }

		//设置sqlManger
		mySqlManager.setDs(ds);
		mySqlManager.setSQLTemplateEngine(mysSqlTemplateEngine);
        mySqlManager.setDbStyle(myDbStyle);
        mySqlManager.setSqlLoader(mySqlLoader);
        mySqlManager.setNc(myNc);
		mySqlManager.setInters(myInters);
		mySqlManager.setMetaDataManager(myMetadataManager);
		if(this.name==null){
			this.name= sqlManagerNameGenerator.nextName();
		}
		mySqlManager.setName(this.name);
		//TODO 配置文件加载
		boolean offsetStartZero = Boolean.parseBoolean(myPs.getProperty("OFFSET_START_ZERO","false"));
       	mySqlManager.offsetStartZero = offsetStartZero;
       	dbStyle.setOffsetStartZero(offsetStartZero);
       	//其他个性化设置
        BeanProcessor myBeanProcessor = this.getBeanProcessor();
		mySqlManager.setDefaultBeanProcessors(myBeanProcessor);
        SqlIdFactory mySqlIdFactory =   this.getSqlIdFactory();
        mySqlManager.setSqlIdFactory(mySqlIdFactory);
        mySqlManager.setClassLoaderKit(myClassLoaderKit);

        dbStyle.config(mySqlManager);
//
//        if(sqlManagerMap.containsKey(this.name)){
//        	throw new IllegalStateException("需要为每一个SQLManager指定一个名称");
//		}
		sqlManagerMap.put(name,mySqlManager);

        return mySqlManager;
    }

    private void check(){
        if(this.ds==null){
            throw new IllegalArgumentException("不能创建SQLManager,至少需要提供 ConnectionSource");
        }
    }

    /**
     * 添加拦截器, 不会与inters冲突, 只会追加
     *
     * @param interceptor 拦截器
     * @return this
     */
    public SQLManagerBuilder addInterceptor(Interceptor interceptor) {
        this.interceptorList.add(interceptor);
        return this;
    }

    /**
     * 添加 DebugInterceptor 不是必须的，但可以通过它查看sql执行情况 <BR>
     * 不会与inters冲突, 只会追加
     *
     * @return this
     */
    public SQLManagerBuilder addInterDebug() {
        this.interceptorList.add(new DebugInterceptor());
        return this;
    }

    private Properties getBeetlPs() {
        if (beetlPs == null) {
            Properties ps = loadDefaultConfig();
            Properties ext = loadExtConfig();
            ps.putAll(ext);
            this.beetlPs = ps;
        }
        return beetlPs;
    }

    /***
     * 加载cfg自定义配置
     *
     * @return
     */
    public Properties loadDefaultConfig() {
        Properties ps = new Properties();
        InputStream ins = this.getClass().getResourceAsStream("/btsql.properties");
        if (ins == null)
            throw new IllegalStateException("默认配置文件加载错:找不到 btsql.properties");;
        try {
            ps.load(ins);
        } catch (IOException e) {
            throw new IllegalStateException("默认配置文件加载错:/btsql.properties");
        }
        return ps;
    }


    public Properties loadExtConfig() {
        Properties ps = new Properties();
        InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("btsql-ext.properties");
        if (ins == null) {
            return ps;
        }

        try {
            ps.load(ins);
            ins.close();
        } catch (IOException e) {
            throw new IllegalStateException("默认配置文件加载错:/btsql-ext.properties");
        }

        return ps;
    }

    /**
     * @param beetlPs 额外的beetl配置
     * @return this
     */
    public SQLManagerBuilder setBeetlPs(Properties beetlPs) {
        if(this.beetlPs==null){
            this.getBeetlPs();
            this.beetlPs.putAll(beetlPs);
        }
        return this;
    }

    public SQLManagerBuilder setBeanProcessor(BeanProcessor beanProcessor) {
        this.beanProcessor = beanProcessor;
        return this;
    }




    private String getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * @param defaultSchema defaultSchema
     * @return this
     */
    public SQLManagerBuilder setDefaultSchema(String defaultSchema,String defaultCatalog) {
        this.defaultSchema = defaultSchema;
        this.defaultCatalog = defaultCatalog;
        this.setSchema = true;
        return this;
    }

	/**
	 * 为sqlManager指定一个名字，如果未指定，则默认"default"
	 * @param name
	 * @return
	 */
	public SQLManagerBuilder setName(String name){
    	this.name = name;
    	return this;
	}

    private Interceptor[] getInters() {
        if (this.inters == null) {
            this.inters = new Interceptor[]{};
        }

        // 添加额外的拦截器
        if (this.interceptorList.size() > 0) {
            Map<String, Interceptor> map = new HashMap<String, Interceptor>();

            for (Interceptor inter : interceptorList) {
                String name = inter.getClass().getName();
                map.put(name, inter);
            }

            for (Interceptor inter : inters) {
                String name = inter.getClass().getName();
                map.put(name, inter);
            }

            inters = new Interceptor[map.size()];
            int i = 0;
            for (Interceptor inter : map.values()) {
                this.inters[i++] = inter;
            }
        }

        return inters;
    }

    /**
     * <pre>
     * 创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
     * Interceptor[] inters = new Interceptor[]{new DebugInterceptor()};
     * </pre>
     *
     * @param inters 拦截器
     * @return this
     */
    public SQLManagerBuilder setInters(Interceptor[] inters) {
        this.inters = inters;
        return this;
    }



    private DBStyle getDbStyle() {
        if (dbStyle == null) {
            dbStyle = new MySqlStyle();
        }
        return dbStyle;
    }

    /**
     * @param dbStyle 数据库风格
     * @return this
     */
    public SQLManagerBuilder setDbStyle(DBStyle dbStyle) {
        this.dbStyle = dbStyle;
        return this;
    }

    private SQLLoader getSqlLoader() {
        if (sqlLoader == null) {
            sqlLoader = new MarkdownClasspathLoader("sql","utf-8");
        }
        return sqlLoader;
    }

    /**
     * @param root sql加载 目录
     * @return this
     */
    public SQLManagerBuilder setSqlLoader(String root,String charset) {
        this.sqlLoader = new MarkdownClasspathLoader(root,charset);
        return this;
    }

    /**
     * @param sqlLoader sql加载
     * @return this
     */
    public SQLManagerBuilder setSqlLoader(SQLLoader sqlLoader) {
        this.sqlLoader = sqlLoader;
        return this;
    }

    private NameConversion getNc() {
        if (nc == null) {
            nc = new DefaultNameConversion();
        }
        return nc;
    }

    private BeanProcessor getBeanProcessor(){
        if(this.beanProcessor==null){
            beanProcessor =  new BeanProcessor();
        }
        return this.beanProcessor;
    }

    public SqlIdFactory getSqlIdFactory(){
        if(sqlIdFactory ==null){
            sqlIdFactory =   new SqlIdFactory();
        }
        return sqlIdFactory;
    }

    public void setSqlIdFactory(SqlIdFactory sqlIdFactory){
        this.sqlIdFactory = sqlIdFactory;
    }

    private SQLTemplateEngine getSqlTemplateEngine(){
        if(sqlTemplateEngine!=null){
            return sqlTemplateEngine;
        }
        sqlTemplateEngine = new BeetlTemplateEngine();
        sqlTemplateEngine.init(this.sqlLoader,beetlPs);
        return sqlTemplateEngine;

    }


    /**
     * @param nc 名字转换器
     * @return this
     */
    public SQLManagerBuilder setNc(NameConversion nc) {
        this.nc = nc;
        return this;
    }

    public ClassLoaderKit getClassLoaderKit() {
        if(classLoaderKit==null){
            classLoaderKit = new ClassLoaderKit();
        }
        return classLoaderKit;
    }



    public void setClassLoaderKit(ClassLoaderKit classLoaderKit) {
        this.classLoaderKit = classLoaderKit;
    }

	/**
	 * 为每个sqlManager生成一个默认名字,系统最好指定每个sql的名字
	 */
	static class SQLManagerNameGenerator{
    	AtomicInteger count = null;
    	public String nextName(){
    		if(count==null){
				count = new AtomicInteger(1);
				return "default";
			}

    		return "default"+count.addAndGet(1);
		}
	}
}


