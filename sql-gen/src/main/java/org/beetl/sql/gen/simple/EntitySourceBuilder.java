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
	public static  String pojoPath = "pojo.btl";
	public static  String pojoAliasPath = "pojoAlias.btl";
	private boolean alias = false;
	public EntitySourceBuilder() {
		super("entity");
	}

	public EntitySourceBuilder(boolean alias) {
		super("entity");
		this.alias = alias;
	}


	@Override
	public void generate(BaseProject project,SourceConfig config, Entity entity) {

		Template template =alias?groupTemplate.getTemplate(pojoAliasPath): groupTemplate.getTemplate(pojoPath);
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
