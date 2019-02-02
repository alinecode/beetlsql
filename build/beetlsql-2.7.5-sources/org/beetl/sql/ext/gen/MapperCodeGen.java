package org.beetl.sql.ext.gen;

import java.io.IOException;

import org.beetl.core.Template;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.kit.GenKit;

public class MapperCodeGen implements CodeGen {
	String pkg = null;
	public MapperCodeGen(){
		
	}
	public MapperCodeGen(String pkg){
		this.pkg = pkg;
	}
	public static String mapperTemplate="";
	static {
		mapperTemplate = GenConfig.getTemplate("/org/beetl/sql/ext/gen/mapper.btl");
	}
	
	@Override
	public void genCode(String entityPkg, String entityClass, TableDesc tableDesc,GenConfig config,boolean isDisplay) {
		if(pkg==null){
			pkg = entityPkg;
		}
		Template template = SourceGen.gt.getTemplate(mapperTemplate);
		String mapperClass = entityClass+"Dao";
		template.binding("className", mapperClass);
		template.binding("package",pkg);
		template.binding("entityClass", entityClass);
		
		String mapperHead = "import "+entityPkg+".*;"+SourceGen.CR;
		template.binding("imports", mapperHead);
		String mapperCode = template.render();
		if(isDisplay){
			System.out.println();
			System.out.println(mapperCode);
		}else{
			try {
				SourceGen.saveSourceFile(GenKit.getJavaSRCPath(), pkg, mapperClass, mapperCode);
			} catch (IOException e) {
				throw new RuntimeException("mapper代码生成失败",e);
			}
		}
		

	}

}
