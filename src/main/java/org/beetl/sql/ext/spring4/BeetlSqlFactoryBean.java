package org.beetl.sql.ext.spring4;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * BeetlSql对工厂Bean的实现用于构建Mapper,一次只对一个接口进行扫描，构建Mapper
 * @param <T>
 * @author woate
 */
public class BeetlSqlFactoryBean<T>  implements FactoryBean<T>,ApplicationContextAware {
    private Class<T> mapperInterface;

    ApplicationContext ctx  = null;
    
    private String sqlManagerName = null;
    
    public BeetlSqlFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public BeetlSqlFactoryBean() {
    }

    @Override
    public T getObject() throws Exception {
    		SQLManager sqlFactory = (SQLManager)ctx.getBean(this.sqlManagerName);
        return sqlFactory.getMapper(mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    //在扫描时会注入该属性
    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.ctx = arg0;
	}

	public String getSqlManagerName() {
		return sqlManagerName;
	}

	public void setSqlManagerName(String sqlManagerName) {
		this.sqlManagerName = sqlManagerName;
	}
    
}
