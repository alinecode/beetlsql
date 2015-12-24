package org.beetl.sql.ext.gen;

/**
 * 生成一个数据库所有pojo代码和模板文件的过滤器，只有返回true的才能生成
 * @author xiandafu
 *
 */
public class GenFilter {
	public boolean accept(String tableName){
		return true;
	}
}
