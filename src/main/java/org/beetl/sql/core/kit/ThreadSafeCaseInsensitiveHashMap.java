package org.beetl.sql.core.kit;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeCaseInsensitiveHashMap extends CaseInsensitiveHashMap {

	private static final long serialVersionUID = 9178606903603606032L;

	private final Map<String, String> lowerCaseMap = new HashMap<String, String>();

	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	@Override
	public boolean containsKey(Object key) {
		try {
			r.lock();
			return super.containsKey(key);
		} finally {
			r.unlock();
		}

	}

	@Override
	public Object get(Object key) {
		try {
			r.lock();
			return super.get(key);
		} finally {
			r.unlock();
		}
	}

	@Override
	public Object put(String key, Object value) {

		/*
		 * 保持map和lowerCaseMap同步 在put新值之前remove旧的映射关系
		 */
		try {
			w.lock();
			return super.put(key, value);
		} finally {
			w.unlock();
		}

	}

	@Override
	public void putAll(Map m) {
		try {
			w.lock();
			super.putAll(m);
		} finally {
			w.unlock();
		}

	}

	@Override
	public Object remove(Object key) {
		try {
			w.lock();
			return super.remove(key);
		} finally {
			w.unlock();
		}

	}
}