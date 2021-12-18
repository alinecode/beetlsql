package org.beetl.sql.gen.simple;

import org.beetl.core.Template;
import org.beetl.sql.gen.BaseProject;
import org.beetl.sql.gen.Entity;
import org.beetl.sql.gen.SourceConfig;

import java.io.Writer;
import java.util.Arrays;

/**
 * 表对应实体的Dao代码，参考md.btl
 */
public class MapperSourceBuilder extends BaseTemplateSourceBuilder {

	/**
	 * 指定模板的路径
	 */
	public static  String mapperPath = "mapper.btl";

	String suffix ;

	public MapperSourceBuilder() {
		super("mapper");
		this.suffix = "Mapper";
	}

	public MapperSourceBuilder(String suffix) {
		super("mapper");
		this.suffix = suffix;
	}

	@Override
	public void generate(BaseProject project, SourceConfig config, Entity entity) {


		Template template = groupTemplate.getTemplate(mapperPath);
		String mapperClass = entity.getName() + suffix;
		template.binding("className", mapperClass);
		template.binding("package", project.getBasePackage(this.name));
		template.binding("entityClass", entity.getName());
		//得到生成的entity的包
		String entityPkg = project.getBasePackage("entity");
		String mapperHead =  entityPkg + ".*" ;
		template.binding("imports", Arrays.asList(mapperHead));
		Writer writer = project.getWriterByName(this.name,entity.getName()+suffix+".java");
		template.renderTo(writer);



	}
}
