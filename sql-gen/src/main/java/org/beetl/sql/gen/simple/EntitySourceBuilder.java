package org.beetl.sql.gen.simple;

import org.beetl.core.Template;
import org.beetl.sql.gen.*;

import java.io.Writer;

/**
 * 表对应实体的Java代码
 */
public class EntitySourceBuilder extends BaseTemplateSourceBuilder {
	/**
	 * 指定模板的路径
	 */
	public static  String mapperPath = "pojo.btl";
	public EntitySourceBuilder() {
		super("entity");
	}


	@Override
	public void generate(BaseProject project,SourceConfig config, Entity entity) {

		Template template = groupTemplate.getTemplate(mapperPath);
		template.binding("attrs", entity.getList());
		template.binding("className", entity.getName());
		template.binding("table", entity.getTableName());
		if(!config.isIgnoreDbCatalog()){
			template.binding("catalog",entity.getCatalog());
		}

		template.binding("package", project.getBasePackage(this.name));
		template.binding("imports", entity.getImportPackage());
		template.binding("comment", entity.getComment());

		Writer writer = project.getWriterByName(this.name,entity.getName()+".java");

		template.renderTo(writer);



	}
}
