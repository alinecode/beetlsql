package org.beetl.sql.clazz.kit;

public interface Cache<K,V>
{

  public V get(K key);

  public void remove(K key);

  public void put(K key, V value);

  public void clearAll();

  public V putIfAbsent(K key, V value);

}
