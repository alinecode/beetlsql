package org.beetl.sql.clazz.kit;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 线程安全的hashMap
 *
 * @author xiandafu
 */
public class ThreadSafeCaseInsensitiveHashMap extends CaseInsensitiveHashMap {

	private static final long serialVersionUID = 9178606903603606032L;


	final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	@Override
	public boolean containsKey(Object key) {
		r.lock();
		try {
			return super.containsKey(key);
		} finally {
			r.unlock();
		}

	}

	@Override
	public Object get(Object key) {
		r.lock();
		try {

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
		w.lock();
		try {
			return super.put(key, value);
		} finally {
			w.unlock();
		}

	}

	@Override
	public void putAll(Map m) {
		w.lock();
		try {

			super.putAll(m);
		} finally {
			w.unlock();
		}

	}

	@Override
	public Object remove(Object key) {
		w.lock();
		try {
			return super.remove(key);
		} finally {
			w.unlock();
		}

	}
}