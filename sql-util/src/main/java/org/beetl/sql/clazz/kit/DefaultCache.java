package org.beetl.sql.clazz.kit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个默认缓存实现.无限制缓存
 * @author xiandafu 
 */
public class DefaultCache<K,V> implements  Cache<K,V> {
  ConcurrentHashMap<K,V> map = new ConcurrentHashMap();
  @Override
  public V get(K key) {
    return map.get(key);
  }

  @Override
  public void remove(K key) {
    map.remove(key);
  }

  @Override
  public void put(K key, V value) {
    map.put(key,value);
  }



  @Override
  public void clearAll() {
    map.clear();
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return map.putIfAbsent(key,value);
  }
}
