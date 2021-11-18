package org.beetl.sql.core;

import javax.sql.DataSource;

/**
 * ConnectionSource提供了路由规则，一般而已，更新走主库，查询走从库。
 * 此类可以设定使用某个数据源来作为后续的操作，用户可以继承DBRunner来扩展数据源的选择，或者使用MasterDBRunner来强制走主数据源，
 * SlaveDBRunner走从数据源
 */
public abstract   class DBRunner<T> {


    /**
     * 使用数据源
     * @param sqlManager
     * @return
     */
    protected  abstract  DataSource getTargetDataSource(SQLManager sqlManager);

    public <T> T  start(SQLManager sm){
        DataSource ds = getTargetDataSource(sm);
        sm.getDs().forceBegin(ds);
        T t = run(sm);
        sm.getDs().forceEnd();
        return t;
    }

    abstract public <T> T  run(SQLManager sm);

    public  abstract  static  class MasterDBRunner<T>  extends DBRunner<T>{

        protected   DataSource getTargetDataSource(SQLManager sqlManager){
            DataSource ds =  sqlManager.getDs().getMasterSource();
            return ds;
        }

    }

    public  abstract  static  class SlaveDBRunner<T>  extends DBRunner<T>{

        protected   DataSource getTargetDataSource(SQLManager sqlManager){
            DataSource[] ds =  sqlManager.getDs().getSlaves();
            if(ds==null){
                throw new IllegalArgumentException(" sqlManager ="+sqlManager.getName()+" 没有从数据源");
            }
            return ds[0];
        }

    }
}
