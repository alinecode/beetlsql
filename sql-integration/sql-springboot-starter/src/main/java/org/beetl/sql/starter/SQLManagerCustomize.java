package org.beetl.sql.starter;

import org.beetl.sql.core.SQLManager;

/**
 * 对sqlManager定制
 * @author xiandafu
 */
public interface SQLManagerCustomize {
	default void beforeCustomize(String sqlManagerName , SQLManager manager){

	}
	public void customize(String sqlManagerName , SQLManager manager);
}
