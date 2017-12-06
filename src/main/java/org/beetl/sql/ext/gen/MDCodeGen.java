package org.beetl.sql.ext.gen;

import java.io.Writer;

import org.beetl.core.Template;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.engine.Beetl;

public class MDCodeGen {
	public MDCodeGen(){
		
	}

	public static String mapperTemplate="";
	static {
		mapperTemplate = GenConfig.getTemplate("/org/beetl/sql/ext/gen/md.btl");
	}
	
	public void genCode(Beetl beetl,TableDesc tableDesc,NameConversion nc,String alias,Writer writer) {
		
		Template template = SourceGen.gt.getTemplate(mapperTemplate);
		
		template.binding("tableName", tableDesc.getName());
		template.binding("cols",tableDesc.getCols());
		template.binding("nc",nc);
		template.binding("alias",alias);
		template.binding("PS", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_START"));
		template.binding("PE", beetl.getPs().getProperty("DELIMITER_PLACEHOLDER_END"));
		template.binding("SS", beetl.getPs().getProperty("DELIMITER_STATEMENT_START"));
		template.binding("SE", beetl.getPs().getProperty("DELIMITER_STATEMENT_END"));
		
		
		
		template.renderTo(writer);
		
	
		

	}

}
