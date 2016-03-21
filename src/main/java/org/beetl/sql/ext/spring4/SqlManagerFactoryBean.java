package org.beetl.sql.ext.spring4;

import org.beetl.core.Function;
import org.beetl.core.TagFactory;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.springframework.util.Assert.notNull;

/**
 * SqlManager创建工厂
 * @author woate，xiandafu
 */
public class SqlManagerFactoryBean implements FactoryBean<SQLManager>, InitializingBean, ApplicationListener<ApplicationEvent> {
    /**
     * BeetlSql数据源
     */
	BeetlSqlDataSource cs;
    /**
     * 数据库样式
     */
    DBStyle dbStyle;
    /**
     * 名称转换样式
     */
    NameConversion nc;
    /**
     * 拦截器
     */
    Interceptor[] interceptors;
    /**
     * BeetlSql核心类
     */
    SQLManager sqlManager;
    
    
    SQLLoader sqlLoader;


    private Map<String, Function> functions = Collections.emptyMap();

    private Map<String, TagFactory> tagFactorys = Collections.emptyMap();

    /**
     * 配置文件地址
     */
    protected Resource configLocation = null;

    protected String defaultSchema = null;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }

    @Override
    public SQLManager getObject() throws Exception {
        if (sqlManager != null) {
            return sqlManager;
        }
        
        //加载数据库
        if(sqlLoader==null){
			sqlLoader = new ClasspathLoader("/sql");
		}
        
        //这里配置拦截器
        if (interceptors == null) {
            interceptors = new Interceptor[0];
        }

        Properties properties = new Properties();

        if (configLocation != null) {
            InputStream in = null;
            try {
                // 如果指定了配置文件，先加载配置文件

                in = configLocation.getInputStream();
                properties.load(in);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {

                    }
                    in = null;
                }
            }
        }
        sqlManager = new SQLManager(dbStyle, sqlLoader, cs, nc, interceptors, this.defaultSchema, properties);


        for (Map.Entry<String, Function> entry : functions.entrySet()) {
            sqlManager.getBeetl().getGroupTemplate().registerFunction(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, TagFactory> entry : tagFactorys.entrySet()) {
            sqlManager.getBeetl().getGroupTemplate().registerTagFactory(entry.getKey(), entry.getValue());
        }
        return sqlManager;
    }

    @Override
    public Class<?> getObjectType() {
        return SQLManager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(cs, "'beetlSqlDataSource'数据源是必须配置的");
      
    }

    /**
     * 配置BeetlSql数据源
     *
     * @param beetlSqlDataSource 数据源对象
     */
    public void setCs(BeetlSqlDataSource beetlSqlDataSource) {
        this.cs = beetlSqlDataSource;
    }

    /**
     * 指定配置文件路径
     *
     * @param configLocation 配置路径
     */
    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

  

    /**
     * 设置数据库方言
     *
     * @param dbStyle 数据库方言
     */
    public void setDbStyle(DBStyle dbStyle) {
        this.dbStyle = dbStyle;
    }

    /**
     * 设置名称转换样式
     *
     * @param nameConversion 名称转换样式
     */
    public void setNc(NameConversion nameConversion) {
        this.nc = nameConversion;
    }

    /**
     * 设置拦截器列表
     *
     * @param interceptors
     */
    public void setInterceptors(Interceptor[] interceptors) {
        this.interceptors = interceptors;
    }


	public void setSqlLoader(SQLLoader sqlLoader) {
		this.sqlLoader = sqlLoader;
	}
    
    
}
