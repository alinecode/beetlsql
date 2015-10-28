package org.beetl.sql.ext.gen;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

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
	static String CR = System.getProperty("line.separator");
	static {
		srcHead+="import java.math.*;"+CR;
		srcHead+="import java.sql.*;"+CR;
//		srcHead+="/** auto gen by beetlsql **/"+CR;
		
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
		StringBuilder body = new StringBuilder();
		TableDesc  tableDesc = mm.getTable(table);
		String className = sm.getNc().getClassName(tableDesc.getMetaName());
		body.append("public class ").append(className);
		if(config.getBaseClass()!=null){
			body.append(" extends ").append(config.getBaseClass());
		}
		body.append("{").append(CR);
		Set<String> cols = tableDesc.getMetaCols();
		for(String col:cols){
		
			ColDesc desc = tableDesc.getColDesc(col);		
			if(desc.remark!=null&&desc.remark.length()!=0){
				body.append(CR);
				body.append(config.getSpace());				
				body.append("/* ").append(desc.remark).append(" */").append(CR);
				
			}
			
			String attrName = sm.getNc().getPropertyName(null, desc.colName);
			String type = JavaType.getType(desc.sqlType, desc.size, desc.digit);
			if(config.isPreferBigDecimal()&&type.equals("Double")){
				type = "BigDecimal";
			}			
			body.append(config.getSpace());
			body.append("private ").append(type).append(" ").append(attrName).append(";").append(CR);
		}
		
		body.append("}");
		StringBuilder code =new StringBuilder();
		code.append("package ").append(pkg).append(";").append(CR);
		code.append(srcHead);
		code.append(body);
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
