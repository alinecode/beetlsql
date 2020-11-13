package org.beetl.sql.saga.common;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLManagerBuilder;

/**
 * 一次sega回滚任务,子类实现必须保证是可序列化的，以方便任务能保存到数据库，或者发送个消息系统
 */
public interface SagaRollbackTask extends java.io.Serializable{
	/**
	 * 回滚任务
	 * @return 如果执行成功，返回true。如果失败，返回false。失败后的策略需要 {@code SegaContext} 的子类实现来考虑
	 * 默认实现将会抛出异常，也就是如果回滚也失败，抛出异常，其他策略可以是尝试重复执行，或者发送给消息队列后再处理
	 */
	 boolean call();


	 default SQLManager getSQLManager(String name){
		 SQLManager sqlManager = SQLManagerBuilder.sqlManagerMap.get(name);
		 if(sqlManager==null){
		 	throw new IllegalStateException("未找到sqlMananager "+name);
		 }
		 return sqlManager;
	 }

}
