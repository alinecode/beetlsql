package org.beetl.sql.gen;

/**
 * 代码生成核心类，用户需要实现子类用于生成代码,Beetlsql提供内置的Pojo,Mapper,MD文件生成
 * @author xiandafu
 * @see  org.beetl.sql.gen.simple.EntitySourceBuilder
 * @see org.beetl.sql.gen.simple.MapperSourceBuilder
 * @see org.beetl.sql.gen.simple.MDSourceBuilder
 * @see org.beetl.sql.gen.simple.BaseTemplateSourceBuilder
 *
 */
public abstract class SourceBuilder {
	/**
	 * 此代码生成的名称
	 */
	protected String name;

	/**
	 * name代表了代码生成类型，比如controller,entity,pojo,query,md等等，
	 * 参考{@link BaseProject#getBasePackage(String)}
	 * 和 {@link BaseProject#getWriterByName(String, String)}}
	 * @param name
	 */
	public SourceBuilder(String name){
		this.name = name;
	}

	/**
	 * 是否支持生成，默认为true，如果返回false，则不会调用{@link #generate}
	 * @param config
	 * @param entity
	 * @return
	 */
	public boolean isSupport(SourceConfig config,Entity entity){
		return true;
	}

	public abstract void  generate(BaseProject project, SourceConfig config,Entity entity);
}
