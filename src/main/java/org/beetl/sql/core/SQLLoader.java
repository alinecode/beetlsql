package org.beetl.sql.core;

import org.beetl.sql.core.db.DBStyle;

public interface SQLLoader {
	/**
	 * 加载sql，如果未找到，抛出错误
	 * @param id
	 * @return
	 */
	public SQLSource getSQL(String id);
	
	/**
	 * 判断一个sql是否修改过
	 * @param id
	 * @return
	 */
	public boolean isModified(String id);
	/**
	 * 判断一个sql是否存在
	 * @param id
	 * @return
	 */
	public boolean exist(String id);
	/**
	 * SQLLoader里增加一个自动生成的SQL
	 * @param id
	 * @param msource
	 */
	public void addSQL(String id,SQLSource msource);
	/**
	 * 每次都检测sql变化
	 * @return
	 */
	public boolean isAutoCheck();
	
	/**
	 * 设置是否检测SQL变化，开发模式下检查
	 * @param check
	 */
	public void setAutoCheck(boolean check);
	
	
	public String getCharset();
	
	public void setCharset(String charset);
	
	/**
	 * sqlId到sql文件的转化
	 * @param sqlIdNc
	 */
	public void setSQLIdNameConversion(SQLIdNameConversion sqlIdNc);
	/**
	 * 设置当前使用的数据库，以让sqlloder优先寻找数据库
	 * @param dbStyle
	 */
	public void setDbStyle(DBStyle dbStyle);
	
	public void refresh();
	
}
