package org.beetl.sql.sega.common;

/**
 * 一次sega回滚任务
 */
public interface SegaRollbackTask {
	 boolean call();
}
