package org.beetl.sql.sega.common;

public class LocalSegaContextFactory implements SegaContextFactory{
	ThreadLocal<LocalSegaContext> local = new ThreadLocal(){
		protected LocalSegaContext initialValue(){
			return new LocalSegaContext();
		}
	};
	@Override
	public LocalSegaContext current() {
		return local.get();
	}
}
