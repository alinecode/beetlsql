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
    	try{
			DataSource ds = getTargetDataSource(sm);
			sm.getDs().forceBegin(ds);
			T t = run(sm);
			return t;
		}finally {
			sm.getDs().forceEnd();
		}

    }

	/**
	 * 子类需要实现此方法，可以使用此SQLManager或者任何SQLManager生成的Mapper来访问数据库
	 * @param sm
	 * @param <T>
	 * @return
	 */
    abstract public <T> T  run(SQLManager sm);

	/**
	 * 强制使用主数据源
	 * @param <T>
	 */
	public  abstract  static  class MasterDBRunner<T>  extends DBRunner<T>{

        protected   DataSource getTargetDataSource(SQLManager sqlManager){
            DataSource ds =  sqlManager.getDs().getMasterSource();
            return ds;
        }

    }

	/**
	 * 强制使用第一个从库
	 * @param <T>
	 */
	public  abstract  static  class SlaveDBRunner<T>  extends DBRunner<T>{

        protected   DataSource getTargetDataSource(SQLManager sqlManager){
            DataSource[] ds =  sqlManager.getDs().getSlaves();
            if(ds==null){
                throw new IllegalArgumentException(" sqlManager ="+sqlManager.getName()+" 没有从数据源");
            }
            return ds[0];
        }

    }

	/**
	 * 遍历所有slave，并执行
	 */
	public abstract  static class EachSlaveDbRunner extends DBRunner<Object>{
		protected    DataSource getTargetDataSource(SQLManager sqlManager){
			throw new UnsupportedOperationException();
		}

		public Object start(SQLManager sm){
			try{
				DataSource[] slaves =  sm.getDs().getSlaves();
				for(DataSource ds:slaves){
					sm.getDs().forceBegin(ds);
					run(sm);
					sm.getDs().forceEnd();
				}
				return null;

			}finally {
				sm.getDs().forceEnd();
			}

		}
	}
}
