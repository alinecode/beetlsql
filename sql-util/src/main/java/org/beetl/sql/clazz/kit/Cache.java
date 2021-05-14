package org.beetl.sql.clazz.kit;

/**
 * 自定义缓存接口
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {

	/**
	 * 获取缓存值
	 * @param key 键
	 * @return 返回缓存值
	 */
	V get(K key);

	/**
	 * 移除缓存
	 * @param key 键
	 */
	void remove(K key);

	/**
	 * 存入缓存
	 * @param key 键
	 * @param value 值
	 */
	void put(K key, V value);

	/**
	 * 清除缓存
	 */
	void clearAll();

	/**
	 * 当缓存不存在已有值时，才存入
	 * @param key 键
	 * @param value 值
	 * @return 如果已有值，返回上一个存入值，否则返回新存入值
	 */
	V putIfAbsent(K key, V value);

}
