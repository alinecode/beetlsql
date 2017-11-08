package org.beetl.sql.ext.gen;

import java.io.Writer;

import org.beetl.core.Template;
import org.beetl.sql.core.NameConversion;
import org.beetl.sql.core.db.TableDesc;

public class MDCodeGen {
	public MDCodeGen(){
		
	}

	public static String mapperTemplate="";
	static {
		mapperTemplate = GenConfig.getTemplate("/org/beetl/sql/ext/gen/md.btl");
	}
	
	public void genCode(TableDesc tableDesc,NameConversion nc,String alias,Writer writer) {
		
		Template template = SourceGen.gt.getTemplate(mapperTemplate);
		
		template.binding("tableName", tableDesc.getName());
		template.binding("cols",tableDesc.getCols());
		template.binding("nc",nc);
		template.binding("alias",alias);
		template.renderTo(writer);
	
		

	}

}
