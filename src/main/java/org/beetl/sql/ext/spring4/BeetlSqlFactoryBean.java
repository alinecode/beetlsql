package org.beetl.sql.ext.spring4;
import org.springframework.beans.factory.FactoryBean;

import static org.springframework.util.Assert.notNull;

/**
 * BeetlSql对工厂Bean的实现用于构建Mapper,一次只对一个接口进行扫描，构建Mapper
 * @param <T>
 * @author woate
 */
public class BeetlSqlFactoryBean<T> extends BeetlSqlDaoSupport implements FactoryBean<T> {
    private Class<T> mapperInterface;

    public BeetlSqlFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public BeetlSqlFactoryBean() {
    }

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        super.checkDaoConfig();
        notNull(this.mapperInterface, " 'mapperInterface' 属性是必须的");
    }

    @Override
    public T getObject() throws Exception {
        return this.sqlManager.getMapper(mapperInterface);
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

}
