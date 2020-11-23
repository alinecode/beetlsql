package org.beetl.sql.gen.simple;

import org.beetl.core.Template;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.engine.template.Beetl;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;
import org.beetl.sql.gen.BaseProject;
import org.beetl.sql.gen.Entity;
import org.beetl.sql.gen.SourceConfig;

import java.io.Writer;

/**
 *
 * @author xiandafu
 */
public class MDSourceBuilder extends BaseTemplateSourceBuilder {
	public static  String mapperTemplate="md.btl";
	public MDSourceBuilder() {
		super("md");
	}

	@Override
	public void generate(BaseProject project, SourceConfig config, Entity entity) {
		//BeetlSQl中的配置
		Beetl beetl = ((BeetlTemplateEngine)config.getSqlManager().getSqlTemplateEngine()).getBeetl();
		//模板
		Template template = groupTemplate.getTemplate(mapperTemplate);
		template.binding("tableName", entity.getTableName());
		template.binding("cols", entity.getCols());
		template.binding("nc", config.getSqlManager().getNc());
		template.binding("PS", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_START"));
		template.binding("PE", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_END"));
		template.binding("SS", beetl.getPs().getProperty("DELIMITER_STATEMENT_START"));
		template.binding("SE", beetl.getPs().getProperty("DELIMITER_STATEMENT_END"));
		String mdFileName = StringKit.toLowerCaseFirstOne(entity.getName())+".md";
		Writer writer = project.getWriterByName(this.name,mdFileName);
		template.renderTo(writer);

	}




}
