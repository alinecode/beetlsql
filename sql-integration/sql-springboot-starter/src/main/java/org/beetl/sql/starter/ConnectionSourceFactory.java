package org.beetl.sql.starter;

import org.beetl.sql.ext.spring.SpringConnectionSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

/**
 * jiazhi.li
 * @author xiandafu
 */
public class ConnectionSourceFactory implements FactoryBean<SpringConnectionSource>,
        ApplicationContextAware
{
    SpringConnectionSource springConnectionSource;
    ApplicationContext applicationContext = null;
    String masterSource;
    String[] slaveSource;
    @Override
    public SpringConnectionSource getObject() throws Exception {
        if(springConnectionSource!=null){
            return springConnectionSource;
        }
        SpringConnectionSource temp = new SpringConnectionSource();
        temp.setMasterSource((DataSource)applicationContext.getBean(masterSource));
        if(slaveSource!=null){
            DataSource[] slaves = new DataSource[slaveSource.length];
            int i =0;
            for(String name:slaveSource){
                slaves[i++] = (DataSource)applicationContext.getBean(name);
            }
            temp.setSlaveSource(slaves);
        }

        springConnectionSource = temp;
        return springConnectionSource;
    }

    @Override
    public Class<?> getObjectType() {
        return SpringConnectionSource.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getMasterSource() {
        return masterSource;
    }

    public void setMasterSource(String masterSource) {
        this.masterSource = masterSource;
    }

    public String[] getSlaveSource() {
        return slaveSource;
    }

    public void setSlaveSource(String[] slaveSource) {
        this.slaveSource = slaveSource;
    }
}
