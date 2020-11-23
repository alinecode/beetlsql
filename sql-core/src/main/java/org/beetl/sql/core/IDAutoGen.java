package org.beetl.sql.core;

import org.beetl.sql.annotation.entity.AssignID;

/**
 * id自动生成
 *
 * @param <T>
 * @author xiandafu
 * @see AssignID
 */
public interface IDAutoGen<T> {
	T nextID(String params);
}
