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
 * 生成markdown格式的数据库说明
 * @author xiandafu
 */
public class MDDocBuilder extends BaseTemplateSourceBuilder {
	public static  String mapperTemplate="doc.btl.md";
	public MDDocBuilder() {
		super("doc");
	}

	@Override
	public void generate(BaseProject project, SourceConfig config, Entity entity) {
		//BeetlSQl中的配置
		Beetl beetl = ((BeetlTemplateEngine)config.getSqlManager().getSqlTemplateEngine()).getBeetl();
		//模板
		Template template = groupTemplate.getTemplate(mapperTemplate);
		template.binding("tableName", entity.getTableName());
		template.binding("comment", entity.getComment());
		template.binding("colsMap", entity.getTableDesc().getColsDetail());
		template.binding("table", entity.getTableDesc());
		String mdFileName = StringKit.toLowerCaseFirstOne(entity.getName())+".doc.md";
		Writer writer = project.getWriterByName(this.name,mdFileName);
		template.renderTo(writer);

	}




}
