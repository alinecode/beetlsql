package org.beetl.sql.clazz.kit;

import java.util.*;

/**
 * 一个大小写不铭感的hashmap，主要是考虑到各个数据的列名大小写不同。key值需要忽略大小写
 * @param <K>
 * @param <V>
 * @author xiandafu
 */
public class CaseInsensitiveHashMap<K,V> extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 9178606903603606031L;
	
	private final Map<String, String> lowerCaseMap = new HashMap<String, String>();

    @Override
    public boolean containsKey(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
        return super.containsKey(realKey);
    }

	@Override
    public Object get(Object key) {
        Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
        return super.get(realKey);
    }

	@Override
    public Set keySet() {
    		return lowerCaseMap.keySet();
    }

	@Override
    public Object put(String key, Object value) {
    	
        /*
         * 保持map和lowerCaseMap同步
         * 在put新值之前remove旧的映射关系
         */
        Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
        Object oldValue = super.remove(oldKey);
        super.put(key, value);
        return oldValue;
    }

	@Override
    public void putAll(Map<? extends String, ?> m) {
        for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            this.put(key, value);
        }
    }

	@Override
    public Object remove(Object key) {
        Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
        return super.remove(realKey);
    }
    
 
}
