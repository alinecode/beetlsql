package org.beetl.sql.core;

import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

import java.util.*;

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
 * <li>sqlLoader : new ClasspathLoader()</li>
 * <li>nc : new DefaultNameConversion()</li>
 * <li>inters : new Interceptor[]{}</li>
 * <li>defaultSchema : null</li>
 * <li>beetlPs : new Properties()</li>
 * </ul>
 * <p>
 * <BR>
 * create time : 2017-04-28 14:44
 *
 * @author luoyizhu@gmail.com
 */
public class SQLManagerBuilder {
    /** 额外的beetl配置 */
    Properties beetlPs;

    /** 拦截器 */
    Interceptor[] inters;

    /** 数据库默认的shcema，对于单个schema应用，无需指定，但多个shcema，需要指定默认的shcema */
    private String defaultSchema;

    /** 数据库风格 */
    private DBStyle dbStyle;

    /** sql加载 */
    private SQLLoader sqlLoader;

    /** 名字转换器 */
    private NameConversion nc;

    /** 数据库连接管理 */
    private ConnectionSource ds;

    /** 拦截器 */
    private List<Interceptor> interceptorList = new LinkedList<Interceptor>();

    SQLManagerBuilder(ConnectionSource ds) {
        this.ds = ds;
    }

    /**
     * @return 构建 SQLManager
     */
    public SQLManager build() {
        DBStyle dbStyle = this.getDbStyle();
        SQLLoader sqlLoader = this.getSqlLoader();
        NameConversion nc = this.getNc();
        Interceptor[] inters = this.getInters();
        String defaultSchema = this.getDefaultSchema();
        Properties ps = this.getBeetlPs();
        SQLManager sm = new SQLManager(dbStyle, sqlLoader, ds, nc, inters, defaultSchema, ps);
        return sm;
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
            this.beetlPs = new Properties();
        }
        return beetlPs;
    }

    /**
     * @param beetlPs 额外的beetl配置
     * @return this
     */
    public SQLManagerBuilder setBeetlPs(Properties beetlPs) {
        this.beetlPs = beetlPs;
        return this;
    }

    private String getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * @param defaultSchema defaultSchema
     * @return this
     */
    public SQLManagerBuilder setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
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
            sqlLoader = new ClasspathLoader();
        }
        return sqlLoader;
    }

    /**
     * @param root sql加载 目录
     * @return this
     */
    public SQLManagerBuilder setSqlLoader(String root) {
        this.sqlLoader = new ClasspathLoader(root);
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

    /**
     * @param nc 名字转换器
     * @return this
     */
    public SQLManagerBuilder setNc(NameConversion nc) {
        this.nc = nc;
        return this;
    }
}
