package org.beetl.sql.core;

import static org.beetl.sql.core.kit.ConstantEnum.DELETE_BY_ID;
import static org.beetl.sql.core.kit.ConstantEnum.DELETE_TEMPLATE_BY_ID;
import static org.beetl.sql.core.kit.ConstantEnum.INSERT;
import static org.beetl.sql.core.kit.ConstantEnum.INSERT_TEMPLATE;
import static org.beetl.sql.core.kit.ConstantEnum.SELECT_ALL;
import static org.beetl.sql.core.kit.ConstantEnum.SELECT_BY_ID;
import static org.beetl.sql.core.kit.ConstantEnum.SELECT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.ConstantEnum.SELECT_COUNT_BY_TEMPLATE;
import static org.beetl.sql.core.kit.ConstantEnum.UPDATE_ALL;
import static org.beetl.sql.core.kit.ConstantEnum.UPDATE_BY_ID;
import static org.beetl.sql.core.kit.ConstantEnum.UPDATE_TEMPLATE_BY_ID;

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
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.kit.CaseInsensitiveOrderSet;
import org.beetl.sql.core.kit.ConstantEnum;
import org.beetl.sql.core.kit.GenKit;
import org.beetl.sql.core.kit.PageKit;
import org.beetl.sql.core.kit.StringKit;
import org.beetl.sql.core.mapper.DefaultMapperBuilder;
import org.beetl.sql.core.mapper.MapperBuilder;
import org.beetl.sql.core.mapper.builder.MapperConfig;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.ext.SnowflakeIDAutoGen;
import org.beetl.sql.ext.gen.GenConfig;
import org.beetl.sql.ext.gen.GenFilter;
import org.beetl.sql.ext.gen.SourceGen;

/**
 * Beetsql 操作入口
 *
 * @author xiandafu
 */
public class SQLManager {

    // 每个sqlManager都有一个标示，可以通过标识来找到对应的sqlManager，用于序列化和反序列化
    private static Map<String, SQLManager> sqlManagerMap = new HashMap<String, SQLManager>();
    Interceptor[] inters = {};
    Beetl beetl = null;
    MapperBuilder mapperBuilder = new DefaultMapperBuilder(this);
    boolean offsetStartZero = false;
    //映射jdbc Result到java对象的工具类，跟sqlId相关
    Map<String, BeanProcessor> processors = new HashMap<String, BeanProcessor>();
    //默认的映射工具类
    BeanProcessor defaultBeanProcessors = null;
    Map<String, IDAutoGen> idAutonGenMap = new HashMap<String, IDAutoGen>();
    private DBStyle dbStyle;
    private SQLLoader sqlLoader;
    private ConnectionSource ds = null;//数据库连接管理
    private NameConversion nc = null;//名字转换器
    private MetadataManager metaDataManager;
    //数据库默认的shcema，对于单个schema应用，无需指定，但多个shcema，需要指定默认的shcema
    private String defaultSchema = null;
    private MapperConfig mapperConfig = new MapperConfig();
    private String sqlMananagerName = null;

    {
        //添加一个id简单实现
        idAutonGenMap.put("simple", new SnowflakeIDAutoGen());
    }

    /**
     * 创建一个beetlsql需要的sqlmanager
     *
     * @param dbStyle
     * @param ds
     */
    public SQLManager(DBStyle dbStyle, ConnectionSource ds) {
        this(dbStyle, new ClasspathLoader("/sql"), ds);

    }

    /**
     * @param dbStyle   数据个风格
     * @param sqlLoader sql加载
     * @param ds        数据库连接
     */
    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds) {
        this(dbStyle, sqlLoader, ds, new DefaultNameConversion(), new Interceptor[]{}, null);

    }

    /**
     * @param dbStyle   数据个风格
     * @param sqlLoader sql加载
     * @param ds        数据库连接
     * @param nc        数据库名称与java名称转化规则
     */
    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds, NameConversion nc) {
        this(dbStyle, sqlLoader, ds, nc, new Interceptor[]{}, null);

    }

    /**
     * @param dbStyle
     * @param sqlLoader
     * @param ds
     * @param nc
     * @param inters
     */
    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds, NameConversion nc, Interceptor[] inters) {
        this(dbStyle, sqlLoader, ds, nc, inters, null);
    }

    /**
     * @param dbStyle
     * @param sqlLoader
     * @param ds
     * @param nc
     * @param inters
     * @param defaultSchema 数据库访问的schema，为null自动判断
     */
    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds, NameConversion nc, Interceptor[] inters, String defaultSchema) {
        this(dbStyle, sqlLoader, ds, nc, inters, defaultSchema, new Properties());
    }

    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds, NameConversion nc, Interceptor[] inters, String defaultSchema, Properties ps) {
        this(dbStyle, sqlLoader, ds, nc, inters, defaultSchema, ps, dbStyle.getName());
    }

    /**
     * @param dbStyle
     * @param sqlLoader
     * @param ds
     * @param nc
     * @param inters
     * @param defaultSchema
     * @param ps            额外的beetl配置
     */
    public SQLManager(DBStyle dbStyle, SQLLoader sqlLoader, ConnectionSource ds, NameConversion nc, Interceptor[] inters, String defaultSchema, Properties ps, String name) {
        this.defaultSchema = defaultSchema;
        beetl = new Beetl(sqlLoader, ps);
        this.dbStyle = dbStyle;
        this.sqlLoader = sqlLoader;
        this.sqlLoader.setDbStyle(dbStyle);
        this.ds = ds;
        this.nc = nc;
        this.inters = inters;
        this.dbStyle.setNameConversion(this.nc);

        this.dbStyle.setMetadataManager(initMetadataManager());
        this.dbStyle.init(beetl);

        offsetStartZero = Boolean.parseBoolean(beetl.getPs().getProperty("OFFSET_START_ZERO").trim());
        defaultBeanProcessors = new BeanProcessor(this);
        //目前假定每个sql都有自己的名字，目前
        sqlMananagerName = name;
        this.sqlManagerMap.put(name, this);
    }

    /**
     * 使用这个创建更加的简洁, 并且用户不需要理解更多的 构造函数
     *
     * @param ds 数据源
     * @return SQLManager构建器
     */
    public static SQLManagerBuilder newBuilder(ConnectionSource ds) {
        return new SQLManagerBuilder(ds);
    }

    /**
     * 快速上手的简洁构建器
     *
     * @param driver   驱动
     * @param url      url
     * @param userName userName
     * @param password password
     * @return SQLManager构建器
     */
    public static SQLManagerBuilder newBuilder(String driver, String url, String userName, String password) {
        ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, userName, password);
        return newBuilder(source);
    }

    /**
     * 每个sqlManager都有个名称，如果未指定，默认是dbStyle 返回的名称，即数据库名
     *
     * @param name
     * @return
     */
    public static SQLManager getSQLManagerByName(String name) {
        SQLManager sqlManager = sqlManagerMap.get(name);
        if (sqlManager == null) {
            throw new NullPointerException("不能根据" + name + "获得sqlManager");
        }
        return sqlManager;
    }

    public boolean isOffsetStartZero() {
        return offsetStartZero;
    }

    /**
     * @param @return
     * @return MetadataManager
     * @throws @MethodName: getMetadataManager
     * @Description: 获取MetaDataManager
     */
    private MetadataManager initMetadataManager() {

        if (metaDataManager == null) {
            metaDataManager = new MetadataManager(this.ds, this);
        }
        return metaDataManager;

    }

    /**
     * 是否是生产模式:生产模式MetadataManager ，不查看sql文件变化,默认是false
     *
     * @return
     */
    public boolean isProductMode() {
        boolean productMode = !sqlLoader.isAutoCheck();
        return productMode;
    }

    /**
     * 不执行数据库操作，仅仅得到一个sql模板执行后的实际得sql和相应的参数
     *
     * @param id
     * @param paras
     * @return
     */
    public SQLResult getSQLResult(String id, Map<String, Object> paras) {
        SQLScript script = getScript(id);
        return script.run(paras);
    }

    /**
     * 不执行数据库操作，仅仅得到一个sql模板执行后的实际得sql和相应的参数
     *
     * @param id
     * @param paras
     * @return
     */
    public SQLResult getSQLResult(String id, Object paras) {
        SQLScript script = getScript(id);
        Map map = new HashMap();
        map.put("_root", paras);
        return script.run(map);
    }

    /**
     * 内部使用，
     *
     * @param source
     * @param inputParas
     * @return
     */
    public SQLResult getSQLResult(SQLSource source, Map inputParas) {
        SQLScript script = new SQLScript(source, this);
        SQLResult result = script.run(inputParas);
        return result;
    }

    /**
     * 内部使用，用于use等函数
     *
     * @param id
     * @param paras
     * @param parentId
     * @return
     */
    public SQLResult getSQLResult(String id, Map<String, Object> paras, String parentId) {
        SQLScript script = getScript(id);
        return script.run(paras, parentId);
    }

    /**
     * 得到指定sqlId的sqlscript对象
     *
     * @param id
     * @return
     */
    public SQLScript getScript(String id) {
        SQLSource source = sqlLoader.getSQL(id);
        SQLScript script = new SQLScript(source, this);
        return script;
    }

    /**
     * 得到增删改查模板
     *
     * @param cls          clz
     * @param constantEnum ConstantEnum
     * @return SQLScript
     */
    public SQLScript getScript(Class<?> cls, ConstantEnum constantEnum) {
        String className = cls.getSimpleName().toLowerCase();
//        String id = className + "." + classSQL[tempId];
        String id = className + "." + constantEnum.getClassSQL();

        SQLSource tempSource = this.sqlLoader.getGenSQL(id);
        if (tempSource != null) {
            return new SQLScript(tempSource, this);
        }
        switch (constantEnum) {
            case SELECT_BY_ID: {
                tempSource = this.dbStyle.genSelectById(cls);
                break;
            }
            case SELECT_BY_TEMPLATE: {
                tempSource = this.dbStyle.genSelectByTemplate(cls);
                break;
            }
            case SELECT_COUNT_BY_TEMPLATE: {
                tempSource = this.dbStyle.genSelectCountByTemplate(cls);
                break;
            }
            case DELETE_BY_ID: {
                tempSource = this.dbStyle.genDeleteById(cls);
                break;
            }
            case SELECT_ALL: {
                tempSource = this.dbStyle.genSelectAll(cls);
                break;
            }
            case UPDATE_ALL: {
                tempSource = this.dbStyle.genUpdateAll(cls);
                break;
            }
            case UPDATE_BY_ID: {
                tempSource = this.dbStyle.genUpdateById(cls);
                break;
            }

            case UPDATE_TEMPLATE_BY_ID: {
                tempSource = this.dbStyle.genUpdateTemplate(cls);
                break;
            }

            case INSERT: {
                tempSource = this.dbStyle.genInsert(cls);
                break;
            }

            case INSERT_TEMPLATE: {
                tempSource = this.dbStyle.genInsertTemplate(cls);
                break;
            }
            case DELETE_TEMPLATE_BY_ID:
                tempSource = this.dbStyle.genDeleteById(cls);
                break;
            default: {
                throw new UnsupportedOperationException();
            }
        }

        tempSource.setId(id);
        sqlLoader.addGenSQL(id, tempSource);
        return new SQLScript(tempSource, this);
    }

    /* ============ 查询部分 ================== */

    /****
     * 获取为分页语句
     *
     * @param selectId
     * @return
     */
    public SQLScript getPageSqlScript(String selectId) {
        String pageId = selectId + "_page";
        if (this.isProductMode()) {
            //产品模式
            SQLSource source = sqlLoader.getGenSQL(pageId);
            if (source != null) {
                return new SQLScript(source, this);
            }
        }

        SQLSource script = sqlLoader.getSQL(selectId);
        if (script == null) {
            script = sqlLoader.getSQL(selectId);
        }

        String template = script.getTemplate();
        String pageTemplate = dbStyle.getPageSQL(template);
        SQLSource source = new SQLSource(pageId, pageTemplate);
        sqlLoader.addGenSQL(pageId, source);
        return new SQLScript(source, this);
    }

    /**
     * 通过sqlId进行查询,查询结果映射到clazz上
     *
     * @param sqlId sql标记
     * @param clazz 需要映射的Pojo类,可以是实体类，也可以是一个Map
     * @param paras 参数集合
     * @return Pojo集合
     */
    public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras) {
        return this.select(sqlId, clazz, paras, null);
    }

    /**
     * 通过sqlId进行查询,查询结果映射到clazz上，mapper类可以定制映射
     *
     * @param sqlId  sql标记
     * @param clazz  需要映射的Pojo类,可以是实体类，也可以是一个Map
     * @param paras  参数集合
     * @param mapper 自定义结果映射方式
     * @return
     */
    public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, RowMapper<T> mapper) {
        SQLScript script = getScript(sqlId);
        return script.select(clazz, paras, mapper);
    }

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
    public <T> List<T> select(String sqlId, Class<T> clazz, Object paras) {
        return this.select(sqlId, clazz, paras, null);
    }

    /**
     * 根据sqlId查询目标对象
     *
     * @param sqlId
     * @param clazz
     * @return
     */
    public <T> List<T> select(String sqlId, Class<T> clazz) {
        return this.select(sqlId, clazz, null, null);
    }

    /**
     * 通过sqlId进行查询:查询结果映射到clazz上，输入条件是个Bean,
     * Bean的属性可以被sql语句引用，如bean中有name属性,即方法getName,则sql语句可以包含name属性， 如select *
     * from xxx where name = #name#。mapper类可以指定结果映射方式
     *
     * @param sqlId  sql标记
     * @param clazz  需要映射的Pojo类
     * @param paras  Bean
     * @param mapper 自定义结果映射方式
     * @return
     */

    public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, RowMapper<T> mapper) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("_root", paras);
        SQLScript script = getScript(sqlId);
        return script.select(clazz, param, mapper);
    }

    /**
     * 翻页查询
     *
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
     *
     * @param sqlId  sql标记
     * @param clazz  需要映射的Pojo类
     * @param paras  Bean
     * @param mapper 自定义结果映射方式
     * @param start  开始位置
     * @param size   查询条数
     * @return Pojo集合
     */
    public <T> List<T> select(String sqlId, Class<T> clazz, Object paras, RowMapper<T> mapper, long start, long size) {
        SQLScript script = getScript(sqlId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_root", paras);
        return script.select(map, clazz, mapper, start, size);
    }

    /**
     * 翻页查询
     *
     * @param sqlId sql标记
     * @param clazz 需要映射的Pojo类
     * @param paras 条件集合
     * @param start 开始位置
     * @param size  查询条数
     * @return
     */
    public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, long start, long size) {

        SQLScript script = getScript(sqlId);
        return script.select(paras, clazz, null, start, size);
    }

    /**
     * 翻页查询
     *
     * @param sqlId  sql标记
     * @param clazz  需要映射的Pojo类
     * @param paras  条件集合
     * @param mapper 自定义结果映射方式
     * @param start  开始位置
     * @param size   查询条数
     * @return
     */
    public <T> List<T> select(String sqlId, Class<T> clazz, Map<String, Object> paras, RowMapper<T> mapper, long start, long size) {
        SQLScript script = getScript(sqlId);
        return script.select(paras, clazz, mapper, start, size);
    }

    public <T> void pageQuery(String sqlId, Class<T> clazz, PageQuery query) {
        pageQuery(sqlId, clazz, query, null);
    }

    /**
     * 翻页查询，假设有sqlId和sqlId$count 俩个sql存在，beetlsql会通过
     * 这俩个sql来查询总数以及翻页操作，如果没有sqlId$count，则假设sqlId 包含了page函数或者标签 ，如
     * <p>
     * </p>
     * <p>
     * <pre>
     * queryUser
     * ===
     * select #page("a.*,b.name")# from user a left join role b ....
     * </pre>
     *
     * @param sqlId
     * @param query
     */
    public <T> void pageQuery(String sqlId, Class<T> clazz, PageQuery query, RowMapper<T> mapper) {
        Object paras = query.getParas();
        Map<String, Object> root = null;
        Long totalRow = query.getTotalRow();
        List<T> list = null;
        if (paras == null) {
            root = new HashMap<String, Object>();
        } else if (paras instanceof Map) {
            root = (Map<String, Object>) paras;
        } else {
            root = new HashMap<String, Object>();
            root.put("_root", paras);
        }

        if (query.getOrderBy() != null) {
            root.put(DBStyle.ORDER_BY, query.getOrderBy());
        }

        String sqlCountId = sqlId.concat("$count");
        boolean hasCountSQL = this.sqlLoader.exist(sqlCountId);
        if (query.getTotalRow() == -1) {
            //需要查询行数
            if (hasCountSQL) {
                totalRow = this.selectSingle(sqlCountId, root, Long.class);
            } else {
                root.put(PageQuery.pageFlag, PageQuery.pageObj);
                //todo: 如果sql并不包含翻页标签，没有报错，会有隐患
                totalRow = this.selectSingle(sqlId, root, Long.class);
            }

            if (totalRow == null) {
                totalRow = 0l;
            }
            query.setTotalRow(totalRow);
        }

        if (!hasCountSQL)
            root.remove(PageQuery.pageFlag);

        if (totalRow != 0) {
            long start = (this.offsetStartZero ? 0 : 1) + (query.getPageNumber() - 1) * query.getPageSize();
            long size = query.getPageSize();
            list = this.select(sqlId, clazz, root, mapper, start, size);
        } else {
            list = Collections.EMPTY_LIST;
        }

        query.setList(list);

    }

    /**
     * 根据主键查询 获取唯一记录，如果纪录不存在，将会抛出异常
     *
     * @param clazz
     * @param pk    主键
     * @return
     */
    public <T> T unique(Class<T> clazz, Object pk) {
        SQLScript script = getScript(clazz, SELECT_BY_ID);
        return script.unique(clazz, null, pk);
    }

    /**
     * 根据主键查询,获取唯一记录，如果纪录不存在，将会抛出异常
     *
     * @param clazz
     * @param mapper 自定义结果映射方式
     * @param pk     主键
     * @return
     */
    public <T> T unique(Class<T> clazz, RowMapper<T> mapper, Object pk) {
        SQLScript script = getScript(clazz, SELECT_BY_ID);
        return script.unique(clazz, mapper, pk);
    }

    /* =========模版查询=============== */

    /**
     * @param clazz
     * @param pk
     * @return 如果没有找到，返回null
     */
    public <T> T single(Class<T> clazz, Object pk) {
        SQLScript script = getScript(clazz, SELECT_BY_ID);
        return script.single(clazz, null, pk);
    }

    /**
     * btsql自动生成查询语句，查询clazz代表的表的所有数据。
     *
     * @param clazz
     * @return
     */
    public <T> List<T> all(Class<T> clazz) {
        SQLScript script = getScript(clazz, SELECT_ALL);
        return script.select(clazz, null);
    }

    /**
     * btsql自动生成查询语句，查询clazz代表的表的所有数据。
     *
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
     *
     * @param clazz
     * @return
     */
    public long allCount(Class<?> clazz) {
        SQLScript script = getScript(clazz, SELECT_COUNT_BY_TEMPLATE);
        return script.selectSingle(null, Long.class);
    }

    /**
     * 查询所有记录
     *
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
     *
     * @param clazz
     * @param mapper
     * @return
     */
    public <T> List<T> all(Class<T> clazz, RowMapper<T> mapper) {
        SQLScript script = getScript(clazz, SELECT_ALL);
        return script.select(clazz, null, mapper);
    }

    public <T> List<T> template(T t) {
        SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("_root", t);
        return (List<T>) script.select(t.getClass(), param, null);
    }

    public <T> T templateOne(T t) {
        // 改为只查询一条记录
        int start = this.offsetStartZero ? 0 : 1;
        List<T> list = template(t, start, start + 1);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public <T> List<T> template(T t, RowMapper mapper) {
        SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("_root", t);
        return (List<T>) script.select(t.getClass(), param, mapper);
    }

    public <T> List<T> template(T t, long start, long size) {
        return this.template(t, null, start, size);
    }

    public <T> List<T> template(T t, RowMapper mapper, long start, long size) {
        SQLScript script = getScript(t.getClass(), SELECT_BY_TEMPLATE);
        SQLScript pageScript = this.getPageSqlScript(script.id);
        Map<String, Object> param = new HashMap<String, Object>();
        this.dbStyle.initPagePara(param, start, size);
        param.put("_root", t);

        return (List<T>) pageScript.select(t.getClass(), param, mapper);
    }

    //========== 取出单个值  ============== //

    /**
     * 查询总数
     *
     * @param t
     * @return
     */
    public <T> long templateCount(T t) {
        SQLScript script = getScript(t.getClass(), SELECT_COUNT_BY_TEMPLATE);
        Long l = script.singleSelect(t, Long.class);
        return l;
    }

    /**
     * 将查询结果返回成Long类型
     *
     * @param id
     * @param paras
     * @return
     */
    public Long longValue(String id, Map<String, Object> paras) {
        return this.selectSingle(id, paras, Long.class);
    }

    /**
     * 将查询结果返回成Long类型
     *
     * @param id
     * @param paras
     * @return
     */
    public Long longValue(String id, Object paras) {
        return this.selectSingle(id, paras, Long.class);
    }

    /**
     * 将查询结果返回成Integer类型
     *
     * @param id
     * @param paras
     * @return
     */
    public Integer intValue(String id, Object paras) {
        return this.selectSingle(id, paras, Integer.class);
    }

    /**
     * 将查询结果返回成Integer类型
     *
     * @param id
     * @param paras
     * @return
     */
    public Integer intValue(String id, Map<String, Object> paras) {
        return this.selectSingle(id, paras, Integer.class);
    }

    /**
     * 将查询结果返回成BigDecimal类型
     *
     * @param id
     * @param paras
     * @return
     */
    public BigDecimal bigDecimalValue(String id, Object paras) {
        return this.selectSingle(id, paras, BigDecimal.class);
    }

    /**
     * 将查询结果返回成BigDecimal类型
     *
     * @param id
     * @param paras
     * @return
     */
    public BigDecimal bigDecimalValue(String id, Map<String, Object> paras) {
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
    public <T> T selectSingle(String sqlId, Object paras, Class<T> target) {
        SQLScript script = getScript(sqlId);
        return script.singleSelect(paras, target);
    }

    /**
     * 返回查询的第一行数据，如果有未找到，返回null
     *
     * @param sqlId
     * @param paras
     * @param target
     * @return
     */
    public <T> T selectSingle(String sqlId, Map<String, Object> paras, Class<T> target) {
        SQLScript script = getScript(sqlId);
        return script.selectSingle(paras, target);
    }

    /**
     * 返回一行数据，如果有多行或者未找到，抛错
     *
     * @param id
     * @param paras
     * @param target
     * @return
     */
    public <T> T selectUnique(String id, Object paras, Class<T> target) {
        SQLScript script = getScript(id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_root", paras);
        return script.selectUnique(map, target);
    }

    /**
     * 返回一行数据，如果有多行或者未找到，抛错
     *
     * @param id
     * @param paras
     * @param target
     * @return
     */
    public <T> T selectUnique(String id, Map<String, Object> paras, Class<T> target) {
        SQLScript script = getScript(id);
        return script.selectUnique(paras, target);
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

        SQLScript script = getScript(clazz, DELETE_BY_ID);
        return script.deleteById(clazz, pkValue);
    }
    
    /**
     * 删除对象, 通过对象的主键
     *
     * @param obj 对象,必须包含了主键，实际上根据主键来删除
     * @return
     */
    public int deleteObject(Object obj) {
    		
        SQLScript script = getScript(obj.getClass(), DELETE_TEMPLATE_BY_ID);
        return script.update(obj);
    }

    //============= 插入 ===================  //

   

    /**
     * 通用插入操作
     *
     * @param paras
     * @return
     */
    public int insert(Object paras) {
        return this.insert(paras.getClass(), paras, false);
    }

    /**
     * 插入实体，且该实体对应的表有自增主键
     *
     * @param paras
     * @param autoDbAssignKey 是否自动从数据库获取主键值
     * @return
     */
    public int insert(Object paras, boolean autoDbAssignKey) {
        return this.insert(paras.getClass(), paras, autoDbAssignKey);
    }

    /**
     * 通用模板插入
     *
     * @param paras
     * @return
     */
    public int insertTemplate(Object paras) {
        return this.insertTemplate(paras.getClass(), paras, false);
    }

    /**
     * 模板插入，并根据autoAssignKey 自动获取自增主键值
     *
     * @param paras
     * @param autoDbAssignKey
     * @return
     */
    public int insertTemplate(Object paras, boolean autoDbAssignKey) {
        return this.insertTemplate(paras.getClass(), paras, autoDbAssignKey);
    }

    /**
     * 对于有自增主键的表，插入一行记录
     *
     * @param clazz
     * @param paras
     * @param autoDbAssignKey，是否获取自增主键
     * @return
     */
    public int insert(Class clazz, Object paras, boolean autoDbAssignKey) {
        return generalInsert(clazz, paras, autoDbAssignKey, false);
    }

    /**
     * 模板插入，非空值插入到数据库，并且获取到自增主键的值
     *
     * @param clazz
     * @param paras
     * @param autoDbAssignKey
     * @return
     */
    public int insertTemplate(Class clazz, Object paras, boolean autoDbAssignKey) {
        return generalInsert(clazz, paras, autoDbAssignKey, true);

    }

    /**
     * 插入对象通用的方法，如果数据表有自增主键，需要获取到自增主键，参考使用 insert(Object paras,boolean
     * autoAssignKey)，或者使用 带有KeyHolder的方法
     *
     * @param clazz
     * @param paras
     * @return
     */
    public int insert(Class<?> clazz, Object paras) {

        return this.insert(clazz, paras, false);
    }

    private int generalInsert(Class clazz, Object paras, boolean autoAssignKey, boolean template) {
        if (autoAssignKey) {
            KeyHolder holder = new KeyHolder();
            Class target = clazz;

            int result = template ? this.insertTemplate(target, paras, holder) : this.insert(target, paras, holder);
            String table = this.nc.getTableName(target);
            ClassDesc desc = this.metaDataManager.getTable(table).getClassDesc(target, nc);

            if (desc.getIdCols().isEmpty()) {
                return result;
            } else {
                Method getterMethod = (Method) desc.getIdMethods().get(desc.getIdAttrs().get(0));

                String name = getterMethod.getName();
                String setterName = name.replaceFirst("get", "set");
                try {
                    Method setterMethod = target.getMethod(setterName, new Class[]{getterMethod.getReturnType()});
                    Object value = holder.getKey();
                    value = BeanKit.convertValueToRequiredType(value, getterMethod.getReturnType());
                    setterMethod.invoke(paras, new Object[]{value});
                    return result;
                } catch (Exception ex) {

                    throw new UnsupportedOperationException("autoAssignKey failure " + ex.getMessage());
                }
            }

        } else {
            SQLScript script = getScript(clazz, template ? INSERT_TEMPLATE : INSERT);
            return script.insert(paras);
        }
    }

    /**
     * 批量插入
     *
     * @param clazz
     * @param list
     */
    public int[] insertBatch(Class clazz, List<?> list) {
        SQLScript script = getScript(clazz, INSERT);
        int[] ret = script.insertBatch(list);
        return ret;
    }

    /**
     * 插入，并获取自增主键的值
     *
     * @param clazz
     * @param paras
     * @param holder
     */
    public int insert(Class<?> clazz, Object paras, KeyHolder holder) {
        SQLScript script = getScript(clazz, INSERT);
        return script.insert(paras, holder);
    }

    /**
     * 模板插入，仅仅插入非空属性，并获取自增主键
     *
     * @param clazz
     * @param paras
     * @param holder
     * @return
     */
    public int insertTemplate(Class<?> clazz, Object paras, KeyHolder holder) {
        SQLScript script = getScript(clazz, INSERT_TEMPLATE);
        return script.insert(paras, holder);
    }

    /**
     * 插入，并获取主键
     *
     * @param sqlId
     * @param paras   参数
     * @param holder
     * @param keyName 主键列名称
     */
    public int insert(String sqlId, Object paras, KeyHolder holder, String keyName) {
        SQLScript script = getScript(sqlId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_root", paras);
        return script.insertBySqlId(map, holder, keyName);
    }

    /**
     * 插入，并获取主键,主键将通过paras所代表的表名来获取
     *
     * @param sqlId
     * @param paras
     * @param holder
     * @return
     */
    public int insert(String sqlId, Object paras, KeyHolder holder) {
        SQLScript script = getScript(sqlId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_root", paras);
        if (holder != null) {
            String tableName = this.nc.getTableName(paras.getClass());
            TableDesc table = this.metaDataManager.getTable(tableName);
            Set<String> idCols = table.getIdNames();
            if (idCols.size() != 1) {
                throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR, "有多个主键，不能自动设置");
            }
            return script.insertBySqlId(map, holder, ((CaseInsensitiveOrderSet) idCols).getFirst());
        } else {
            return script.insertBySqlId(map, null, null);
        }

    }

    /**
     * 插入操作，数据库自增主键放到keyHolder里
     *
     * @param sqlId
     * @param clazz
     * @param paras
     * @param holder
     * @return
     */
    public int insert(String sqlId, Class<?> clazz, Map paras, KeyHolder holder) {
        SQLScript script = getScript(sqlId);
        if (holder != null) {
            String tableName = this.nc.getTableName(clazz);
            TableDesc table = this.metaDataManager.getTable(tableName);
            ClassDesc clsDesc = table.getClassDesc(this.nc);
            Set<String> idCols = table.getIdNames();
            if (idCols.size() != 1) {
                throw new BeetlSQLException(BeetlSQLException.ID_EXPECTED_ONE_ERROR, "有多个主键，不能自动设置");
            }
            return script.insertBySqlId(paras, holder, ((CaseInsensitiveOrderSet) idCols).getFirst());
        } else {
            return script.insertBySqlId(paras, holder, null);

        }
    }

    /**
     * 插入，并获取自增主键值，因为此接口并未指定实体对象，因此需要keyName来指明数据库主键列
     *
     * @param sqlId
     * @param paras
     * @param holder
     * @param keyName 主键列名称
     */
    public int insert(String sqlId, Map paras, KeyHolder holder, String keyName) {
        SQLScript script = getScript(sqlId);
        return script.insertBySqlId(paras, holder, keyName);
    }

    /**
     * 更新一个对象
     *
     * @param obj
     * @return
     */
    public int updateById(Object obj) {
        SQLScript script = getScript(obj.getClass(), UPDATE_BY_ID);
        return script.update(obj);
    }

    /**
     * 为null的值不参与更新，如果想更新null值，请使用updateById
     *
     * @param obj
     * @return 返回更新的条数
     */
    public int updateTemplateById(Object obj) {
        SQLScript script = getScript(obj.getClass(), UPDATE_TEMPLATE_BY_ID);
        return script.update(obj);
    }

    /**
     * @param c     c对应的表名
     * @param paras 参数，仅仅更新paras里包含的值，paras里必须带有主键的值作为更新条件
     * @return 返回更新的条数
     */
    public int updateTemplateById(Class c, Map paras) {
        SQLScript script = getScript(c, UPDATE_TEMPLATE_BY_ID);
        return script.update(paras);
    }

    /****
     * 批量更新
     *
     * @param list ,包含pojo（不支持map）
     * @return
     */
    public int[] updateByIdBatch(List<?> list) {
        if (list == null || list.isEmpty()) {
            return new int[0];
        }
        SQLScript script = getScript(list.get(0).getClass(), UPDATE_BY_ID);
        return script.updateBatch(list);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @param obj
     * @return 返回更新的条数
     */
    public int update(String sqlId, Object obj) {
        SQLScript script = getScript(sqlId);
        return script.update(obj);
    }

    /**
     * 执行sql更新（或者删除）操作
     *
     * @param sqlId
     * @return 返回更新的条数
     */
    public int update(String sqlId) {
        SQLScript script = getScript(sqlId);
        return script.update(null);
    }

    /**
     * 执行sql更新（或者删除语句)
     *
     * @param sqlId
     * @param paras
     * @return 返回更新的条数
     */
    public int update(String sqlId, Map<String, Object> paras) {
        SQLScript script = getScript(sqlId);
        return script.update(paras);
    }

    /**
     * 对pojo批量更新执行sql更新语句，list包含的对象是作为参数，所有属性参与更新
     *
     * @param sqlId
     * @param list
     * @return 返回更新的条数
     */
    public int[] updateBatch(String sqlId, List<?> list) {
        SQLScript script = getScript(sqlId);
        return script.updateBatch(list);
    }

    /**
     * 批量模板更新方式，list包含的对象是作为参数，非空属性参与更新
     *
     * @param clz
     * @param list
     * @return
     */
    public int[] updateBatchTemplateById(Class clz, List<?> list) {
        SQLScript script = getScript(clz, UPDATE_TEMPLATE_BY_ID);
        return script.updateBatch(list);
    }

    /**
     * 批量更新
     *
     * @param sqlId
     * @param maps  参数放在map里
     * @return
     */
    public int[] updateBatch(String sqlId, Map<String, Object>[] maps) {
        SQLScript script = getScript(sqlId);
        return script.updateBatch(maps);
    }

    /**
     * 更新指定表
     *
     * @param clazz
     * @param param 参数
     * @return
     */
    public int updateAll(Class<?> clazz, Object param) {

        SQLScript script = getScript(clazz, UPDATE_ALL);
        return script.update(param);
    }

    /**
     * 只使用master执行:
     * <p>
     * <pre>
     *    sqlManager.useMaster(new DBRunner(){
     *    		public void run(SQLManager sqlManager){
     *          	sqlManager.select .....
     *          }
     *    )
     * </pre>
     *
     * @param f
     */
    public void useMaster(DBRunner f) {
        f.start(this, true);
    }

    /**
     * 只使用Slave执行:
     * <p>
     * <pre>
     *    sqlManager.useSlave(new DBRunner(){
     *    		public void run(SQLManager sqlManager){
     *          	sqlManager.select .....
     *          }
     *    )
     * </pre>
     *
     * @param f
     */
    public void useSlave(DBRunner f) {
        f.start(this, false);
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

        Map map = new HashMap();
        map.put("_root", paras);
        return this.execute(sqlTemplate, clazz, map);
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
        String key = "auto._gen_" + sqlTemplate;
        SQLSource source = sqlLoader.getGenSQL(key);
        if (source == null) {
            source = new SQLSource(key, sqlTemplate);
            this.sqlLoader.addGenSQL(key, source);
        }

        SQLScript script = new SQLScript(source, this);
        return script.select(clazz, paras);
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
    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Map paras, long start, long size) {
        String key = "auto._gen_" + sqlTemplate;
        SQLSource source = sqlLoader.getGenSQL(key);
        if (source == null) {
            String pageSql = this.dbStyle.getPageSQL(sqlTemplate);
            source = new SQLSource(key, pageSql);
            this.sqlLoader.addGenSQL(key, source);
        }

        this.dbStyle.initPagePara(paras, start, size);
        SQLScript script = new SQLScript(source, this);
        return script.select(clazz, paras);
    }

    /**
     * 直接执行sql模板查询，并获取指定范围的结果集
     *
     * @param sqlTemplate
     * @param clazz
     * @param paras
     * @param start
     * @param size
     * @return
     */
    public <T> List<T> execute(String sqlTemplate, Class<T> clazz, Object paras, long start, long size) {
        Map map = new HashMap();
        map.put("_root", paras);
        return this.execute(sqlTemplate, clazz, map, start, size);
    }

    /**
     * 直接执行sql更新，sql是模板
     *
     * @param sqlTemplate
     * @param paras
     * @return
     */
    public int executeUpdate(String sqlTemplate, Object paras) {
        String key = "auto._gen_" + sqlTemplate;
        SQLSource source = sqlLoader.getGenSQL(key);
        if (source == null) {
            source = new SQLSource(key, sqlTemplate);
            this.sqlLoader.addGenSQL(key, source);
        }

        SQLScript script = new SQLScript(source, this);
        Map map = new HashMap();
        map.put("_root", paras);
        return script.update(map);
    }

    /**
     * 直接更新sql，sql是模板
     *
     * @param sqlTemplate
     * @param paras
     * @return
     */
    public int executeUpdate(String sqlTemplate, Map paras) {
        String key = "auto._gen_" + sqlTemplate;
        SQLSource source = sqlLoader.getGenSQL(key);
        if (source == null) {
            source = new SQLSource(key, sqlTemplate);
            this.sqlLoader.addGenSQL(key, source);
        }
        SQLScript script = new SQLScript(source, this);
        return script.update(paras);
    }

    /**
     * 直接执行sql语句查询，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param clazz
     * @param p
     * @return 返回查询结果
     */
    public <T> List<T> execute(SQLReady p, Class<T> clazz) {
        SQLSource source = new SQLSource("native." + p.getSql(), p.getSql());
        SQLScript script = new SQLScript(source, this);
        return script.sqlReadySelect(clazz, p);
    }

    public <T> PageQuery<T> executePageQuery(SQLReady p, Class<T> clazz, long pageNumber, long pageSize) {
    		String sql = p.getSql();
    		String countSql = PageKit.getCountSql(sql);
    		List<Long> countList = execute(new SQLReady(countSql,p.getArgs()),Long.class);
    		Long count = countList.get(0);
    		List<T> list = null;
    		if(count==0){
    			list = Collections.emptyList();
    		}else{
    			long offset = (pageNumber-1)*pageSize;
    			String pageSql = this.dbStyle.getPageSQLStatement(countSql, offset, pageSize);
    			list  = execute(new SQLReady(pageSql,p.getArgs()),clazz);
    		}
    		
    		PageQuery pageQuery = new PageQuery(pageNumber,null,count);
    		pageQuery.setList(list);
    	    return pageQuery;

    }

    /**
     * 直接执行sql语句，用于删除或者更新，sql语句已经是准备好的，采用preparedstatment执行
     *
     * @param p
     * @return 返回更新条数
     */
    public int executeUpdate(SQLReady p) {
        SQLSource source = new SQLSource("native." + p.getSql(), p.getSql());
        SQLScript script = new SQLScript(source, this);
        return script.sqlReadyExecuteUpdate(p);
    }

    //========= 代码生成 =============//

    /**
     * 自己用Connection执行jdbc，通常用于存储过程调用，或者需要自己完全控制的jdbc
     *
     * @param onConnection
     * @return
     */
    public <T> T executeOnConnection(OnConnection<T> onConnection) {
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
                    if (!conn.getAutoCommit()) {
                        conn.commit();
                    }
                    conn.close();

                } catch (SQLException e) {
                    throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION, e);
                }

            }
        }
    }

    /**
     * 根据表名生成对应的pojo类
     *
     * @param table    表名
     * @param pkg      包名,如 com.test
     * @param srcPath: 文件保存路径
     * @param config   配置生成的风格
     * @throws Exception
     */
    public void genPojoCode(String table, String pkg, String srcPath, GenConfig config) throws Exception {
        SourceGen gen = new SourceGen(this, table, pkg, srcPath, config);
        gen.gen();
    }

    /**
     * 同上，但路径自动根据项目当前目录推测，是src目录下，或者src/main/java 下
     *
     * @param table
     * @param pkg
     * @param config
     * @throws Exception
     */
    public void genPojoCode(String table, String pkg, GenConfig config) throws Exception {
        String srcPath = GenKit.getJavaSRCPath();
        SourceGen gen = new SourceGen(this, table, pkg, srcPath, config);
        gen.gen();
    }

    /**
     * 生成pojo类,默认路径是当前工程src目录,或者是src/main/java 下
     *
     * @param table
     * @param pkg
     * @throws Exception
     */
    public void genPojoCode(String table, String pkg) throws Exception {
        String srcPath = GenKit.getJavaSRCPath();
        SourceGen gen = new SourceGen(this, table, pkg, srcPath, new GenConfig());
        gen.gen();
    }

    /**
     * 仅仅打印pojo类到控制台
     *
     * @param table
     * @throws Exception
     */
    public void genPojoCodeToConsole(String table) throws Exception {
        String pkg = SourceGen.defaultPkg;
        String srcPath = System.getProperty("user.dir");
        SourceGen gen = new SourceGen(this, table, pkg, srcPath, new GenConfig().setDisplay(true));
        gen.gen();
    }

    /**
     * 仅仅打印pojo类到控制台
     *
     * @param table
     * @throws Exception
     */
    public void genPojoCodeToConsole(String table, GenConfig config) throws Exception {
        String pkg = SourceGen.defaultPkg;
        String srcPath = System.getProperty("user.dir");
        config.setDisplay(true);
        SourceGen gen = new SourceGen(this, table, pkg, srcPath, config);
        gen.gen();
    }

    /**
     * 将sql模板文件输出到src下，如果采用的是ClasspathLoader，则使用ClasspathLoader的配置，否则，生成到src的sql代码里
     *
     * @param table
     */
    public void genSQLFile(String table) throws Exception {
        String path = "/sql";
        if (this.sqlLoader instanceof ClasspathLoader) {
            path = ((ClasspathLoader) sqlLoader).sqlRoot;
        }
        String fileName = StringKit.toLowerCaseFirstOne(this.nc.getClassName(table));
        String target = GenKit.getJavaResourcePath() + "/" + path + "/" + fileName + ".md";
        FileWriter writer = new FileWriter(new File(target));
        genSQLTemplate(table, writer);
        writer.close();
        System.out.println("gen \"" + table + "\" success at " + target);
    }

    /**
     * 生成sql语句片段,包含了条件查询，列名列表，更新，插入等语句
     *
     * @param table
     */
    public void genSQLTemplateToConsole(String table) throws Exception {

        genSQLTemplate(table, new OutputStreamWriter(System.out));

    }

    private void genSQLTemplate(String table, Writer w) throws IOException {
        String template = null;
        Configuration cf = beetl.getGroupTemplate().getConf();

        String hs = cf.getPlaceholderStart();
        String he = cf.getPlaceholderEnd();
        StringBuilder cols = new StringBuilder();
        String sql = "select " + hs + "use(\"cols\")" + he + " from " + table + " where " + hs + "use(\"condition\")" + he;
        cols.append("sample").append("\n===\n").append("* 注释").append("\n\n\t").append(sql);
        cols.append("\n");

        cols.append("\ncols").append("\n===\n").append("").append("\n\t").append(this.dbStyle.genColumnList(table));
        cols.append("\n");

        cols.append("\nupdateSample").append("\n===\n").append("").append("\n\t").append(this.dbStyle.genColAssignPropertyAbsolute(table));
        cols.append("\n");
        String condition = this.dbStyle.genCondition(table);
        condition = condition.replaceAll("\\n", "\n\t");
        cols.append("\ncondition").append("\n===\n").append("").append("\n\t").append(condition);
        cols.append("\n");
        w.write(cols.toString());
        w.flush();
    }

    /**
     * 生成数据库的所有entity，dao，还有md文件，
     *
     * @param pkg
     * @param config
     * @param filter 最好设置filter以避免覆盖已有代码
     */
    public void genALL(String pkg, GenConfig config, GenFilter filter) throws Exception {
        Set<String> tables = this.metaDataManager.allTable();

        for (String table : tables) {
            table = metaDataManager.getTable(table).getName();
            if (filter == null || filter.accept(table)) {
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

    /**
     * 生成内置的sql，插入，更新，主键查找，删除语句
     *
     * @param cls
     */
    public void genBuiltInSqlToConsole(Class cls) {
        StringBuilder sb = new StringBuilder();
        SQLSource tempSource = this.dbStyle.genSelectById(cls);
        sb.append(tempSource.getTemplate());
        sb.append("\n\r");

        tempSource = this.dbStyle.genUpdateById(cls);
        sb.append(tempSource.getTemplate());
        sb.append("\n\r");
        tempSource = this.dbStyle.genDeleteById(cls);
        sb.append(tempSource.getTemplate());
        sb.append("\n\r");

        tempSource = this.dbStyle.genInsert(cls);
        sb.append(tempSource.getTemplate());
        sb.append("\n\r");

        System.out.println(sb);

    }

    //===============get/set===============

    /**
     * 通过mapper接口生成dao代理
     *
     * @param mapperInterface
     * @return
     */
    public <T> T getMapper(Class<T> mapperInterface) {
        return this.mapperBuilder.getMapper(mapperInterface);
    }

    public SQLLoader getSqlLoader() {
        return sqlLoader;
    }

    public void setSqlLoader(SQLLoader sqlLoader) {
        this.sqlLoader = sqlLoader;
    }

    public ConnectionSource getDs() {
        return ds;
    }

    /**
     * 设置ConnectionSource，参考ConnectionSourceHelper
     *
     * @param ds
     */
    public void setDs(ConnectionSource ds) {
        this.ds = ds;
    }

    /**
     * 获取 NameConversion
     *
     * @return
     */
    public NameConversion getNc() {
        return nc;
    }

    /**
     * 设置NameConversion
     *
     * @param nc
     */
    public void setNc(NameConversion nc) {
        this.nc = nc;
        this.dbStyle.setNameConversion(nc);
    }

    /**
     * 得到当前sqlmanager的数据库类型
     *
     * @return
     */
    public DBStyle getDbStyle() {
        return dbStyle;
    }

    /**
     * 得到beetl引擎
     *
     * @return
     */
    public Beetl getBeetl() {
        return beetl;
    }

    /**
     * 得到MetaDataManager，用来获取数据库元数据，如表，列，主键等信息
     *
     * @return
     */
    public MetadataManager getMetaDataManager() {
        return metaDataManager;
    }

    public String getDefaultSchema() {

        return defaultSchema;
    }

    /**
     * 设置对应的数据库的schema，一般不需要调用，因为通过jdbc能自动获取
     *
     * @param defaultSchema
     */
    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    /**
     * 得到MapperBuilder,默认是DefaultMapperBuilder
     *
     * @return
     */
    public MapperBuilder getMapperBuilder() {
        return mapperBuilder;
    }

    /**
     * 设置MapperBuilder，用来生成java的dao代理类，参考getMapper
     *
     * @param mapperBuilder
     */
    public void setMapperBuilder(MapperBuilder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
    }

    /**
     * 得到所有的Interceptor
     *
     * @return
     */
    public Interceptor[] getInters() {
        return inters;
    }

    /**
     * 设置Interceptor
     *
     * @param inters
     */
    public void setInters(Interceptor[] inters) {
        this.inters = inters;
    }

    /**
     * 设置一种id算法用于注解AssignId("xxx"),这样，对于应用赋值主键，交给beetlsql来处理了
     *
     * @param name
     * @param alorithm
     */
    public void addIdAutonGen(String name, IDAutoGen alorithm) {
        this.idAutonGenMap.put(name, alorithm);
    }

    /**
     * 根据某种算法自动计算id
     *
     * @param name
     * @param param
     * @return
     */
    protected Object getAssignIdByIdAutonGen(String name, String param, String table) {
        IDAutoGen idGen = idAutonGenMap.get(name);
        if (idGen == null) {
            throw new BeetlSQLException(BeetlSQLException.ID_AUTOGEN_ERROR, "未发现自动id生成器:" + name + " in " + table);
        }
        return idGen.nextID(param);

    }

    /**
     * 获取特殊的BeanPorcessor
     *
     * @return
     */
    public Map<String, BeanProcessor> getProcessors() {
        return processors;
    }

    /**
     * 为指定的sqlId提供一个处理类，可以既可以是一个sqlId，也可以是namespace部分，所有属于namesapce的都会被此BeanProcessor
     * 处理
     *
     * @param processors
     */
    public void setProcessors(Map<String, BeanProcessor> processors) {
        this.processors = processors;
    }

    /**
     * 得到默认的jdbc到bean的处理类
     *
     * @return
     */
    public BeanProcessor getDefaultBeanProcessors() {
        return defaultBeanProcessors;
    }

    /**
     * 设置默认的jdbc 到 bean的映射处理类，用户可以自己扩展处理最新的类型
     *
     * @param defaultBeanProcessors
     */
    public void setDefaultBeanProcessors(BeanProcessor defaultBeanProcessors) {
        this.defaultBeanProcessors = defaultBeanProcessors;
    }

    /**
     * 设置sqlId到sql文件映射关系
     *
     * @param sqlIdNc
     */
    public void setSQLIdNameConversion(SQLIdNameConversion sqlIdNc) {
        this.sqlLoader.setSQLIdNameConversion(sqlIdNc);
    }

    public MapperConfig getMapperConfig() {
        return mapperConfig;
    }

    /**
     * @param c 设置一个基接口, 也是推荐的编程方式, 这样可以与框架解耦
     */
    public MapperConfig setBaseMapper(Class c) {
        this.mapperConfig = new MapperConfig(c);
        return this.mapperConfig;
    }

    public String getSQLManagerName() {
        return this.sqlMananagerName;
    }

}
