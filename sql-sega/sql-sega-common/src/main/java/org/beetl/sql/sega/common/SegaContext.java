package org.beetl.sql.sega.common;

public abstract class SegaContext {
	/**
	 * 特定框架必须实现SegaContextFactory，以及SegaContext子类
	 */
	public static SegaContextFactory segaContextFactory = new SegaContextFactory() {
		public LocalSegaContext current() {
			throw new UnsupportedOperationException("必须设置SegaContextFactory实现类,比如LocalSegaContextFactory");
		}
	};
	public abstract void rollback();
	public abstract SegaTransaction getTransaction();
}
