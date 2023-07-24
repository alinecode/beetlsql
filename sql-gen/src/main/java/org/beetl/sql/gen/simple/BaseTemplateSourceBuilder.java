package org.beetl.sql.gen.simple;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.sql.core.engine.template.BeetlSQLTemplateSecurityManager;
import org.beetl.sql.gen.SourceBuilder;

import java.io.IOException;

/**
 * 一个模板生成代码的方式，使用Beetl+模板方式，你可以使用别的模板技术或者javapoet这种生成技术
 * 代码模板在org/beetl/sql/gen/sample下，如果你想用自己的模板，你需要调用{@link #getGroupTemplate()}
 * 设置一下个新的{@link org.beetl.core.ResourceLoader}
 * @author xiandafu
 * @see "http://ibeetl.com"
 */
public abstract  class BaseTemplateSourceBuilder extends SourceBuilder {

	static  protected GroupTemplate groupTemplate = initTemplateEngine();
	public BaseTemplateSourceBuilder(String name){
		super(name);
	}
	/**
	 * 初始化模板引擎
	 */
	static protected  GroupTemplate initTemplateEngine(){

		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("templates/");
		Configuration cfg = null;
		try {
			cfg = Configuration.defaultConfiguration();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, cfg);
		groupTemplate.setNativeSecurity(new BeetlSQLTemplateSecurityManager());
		return groupTemplate;
	}

	/**
	 * 得到GroupTemplate，如果你需要修改ResourceLoader，添加自定义方法等等，可以调用此方法
	 * 获取GroupTemplate
	 * @return
	 */
	public static  GroupTemplate getGroupTemplate(){
		return groupTemplate;
	}






}
