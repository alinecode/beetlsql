package org.beetl.sql.core;

import org.beetl.sql.annotation.entity.AssignID;

/**
 * id自动生成
 * @author xiandafu
 * @param <T>
 * @see AssignID
 */
public interface  IDAutoGen<T> {
		public T nextID(String params);
}
