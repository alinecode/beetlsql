package org.beetl.sql.ext.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.ColDesc;
import org.beetl.sql.core.db.MetadataManager;
import org.beetl.sql.core.db.TableDesc;

public class SourceGen {
	MetadataManager mm;
	SQLManager sm ;
	String table;
	String pkg;
	String srcPath;
	GenConfig config;
	public static String srcHead ="";
	public static String defaultPkg = "com.test";
	static String CR = System.getProperty("line.separator");
	static GroupTemplate gt = null;
	static {
		Configuration conf = null;
		try {
			conf = Configuration.defaultConfiguration();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conf.setStatementStart("@");
		conf.setStatementEnd(null);
		gt = new GroupTemplate(new StringTemplateResourceLoader(),conf);
		srcHead+="import java.math.*;"+CR;
		srcHead+="import java.sql.*;";

		
	}
	
	public SourceGen(SQLManager sm,String table,String pkg,String srcPath,GenConfig config){
		this.mm = sm.getMetaDataManager();
		this.sm = sm;
		this.table = table;
		this.pkg = pkg;
		this.srcPath = srcPath;
		this.config = config;
	}
	/**
	 * 生成代码
	 * 
	 */
	public void gen() throws Exception{
		TableDesc  tableDesc = mm.getTable(table);
		String className = sm.getNc().getClassName(tableDesc.getMetaName());
		String ext = null;
		
		if(config.getBaseClass()!=null){
			ext = config.getBaseClass();
		}
		
		Set<String> cols = tableDesc.getMetaCols();
		List<Map> attrs = new ArrayList<Map>();
		for(String col:cols){
			
			ColDesc desc = tableDesc.getColDesc(col);
			Map attr = new HashMap();
			attr.put("comment", desc.remark);
			attr.put("name", sm.getNc().getPropertyName(null, desc.colName));
			attr.put("type", desc.remark);
			
			String type = JavaType.getType(desc.sqlType, desc.size, desc.digit);
			if(config.isPreferBigDecimal()&&type.equals("Double")){
				type = "BigDecimal";
			}			
			attr.put("type", type);
			attrs.add(attr);
		}
		
		Template template = gt.getTemplate(config.template);
		template.binding("attrs", attrs);
		template.binding("className", className);
		template.binding("ext", ext);
		template.binding("package", pkg);
		template.binding("imports", srcHead);
		template.binding("comment", tableDesc.getRemark());
		String code = template.render();
		if(config.isDisplay()){
			System.out.println(code);
		}else{
//			new File(srcPath).mkdirs();
			String file = srcPath+File.separator+pkg.replace('.',File.separatorChar);
			File f  = new File(file);
			f.mkdirs();
			File target = new File(file,className+".java");
			FileWriter writer = new FileWriter(target);
			writer.write(code.toString());
			writer.close();
		}
	
		
	}
	
}

