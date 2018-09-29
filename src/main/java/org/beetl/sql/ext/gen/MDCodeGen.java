package org.beetl.sql.ext.gen;

import org.beetl.core.Template;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.engine.Beetl;

import java.io.Writer;

public class MDCodeGen {

	private String mapperTemplate = "";

	public MDCodeGen() {
		this.mapperTemplate = new GenConfig().getTemplate("/org/beetl/sql/ext/gen/md.btl");
	}

	public String getMapperTemplate() {
		return mapperTemplate;
	}

	public void setMapperTemplate(String mapperTemplate) {
		this.mapperTemplate = mapperTemplate;
	}

	public void genCode(Beetl beetl, TableDesc tableDesc, NameConversion nc, String alias, Writer writer) {

		Template template = SourceGen.getGt().getTemplate(mapperTemplate);

		template.binding("tableName", tableDesc.getName());
		template.binding("cols", tableDesc.getCols());
		template.binding("idNames", tableDesc.getIdNames());
		template.binding("nc", nc);
		template.binding("alias", alias);
		template.binding("PS", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_START"));
		template.binding("PE", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_END"));
		template.binding("SS", beetl.getPs().getProperty("DELIMITER_STATEMENT_START"));
		template.binding("SE", beetl.getPs().getProperty("DELIMITER_STATEMENT_END"));

		template.renderTo(writer);

	}

}
