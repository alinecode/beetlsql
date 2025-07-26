package org.beetl.sql.starter;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.ext.spring.SQLManagerLifeCycle;

/**
 * 对sqlManager定制
 * @author xiandafu
 * @deprecated  建议直接使用SQLManagerAfterInit
 */
@Deprecated
public interface SQLManagerCustomize extends SQLManagerLifeCycle {

	 void customize(String sqlManagerName , SQLManager manager);
}
