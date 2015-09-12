package org.beetl.sql.core;

import java.sql.Connection;
import java.util.List;

public interface ConnectionSource {
	/**
	 *  得到一个主库链接,用于序列,medata等
	 * @return
	 */
	public Connection getMaster();
	
	
	public Connection getSlave();
	/**
	 * 根据条件得到链接
	 * @param sqlId 
	 * @param isUpdate 是否更新数据
	 * @param sql
	 * @param paras
	 * @return
	 */
	public Connection getConn(String sqlId,boolean isUpdate,String sql,List<?> paras);
	
	/**
	 * 强迫使用master
	 */
	public void onlyMasterBegin();
	
	/**
	 * 结束master选择，如果调用了onlyMasterBegin，则必须调用onlyMasterEnd
	 */
	public void onlyMasterEnd();
	
	
	

	
	public boolean isTransaction();
	
	
	
	
	
}
