package org.beetl.sql.core;
/**
 *  可以设置pojo的额外属性
 * @author xiandafu
 *
 */
public interface Tail {
	public Object get(String key);
	public void set(String key,Object value);
}
