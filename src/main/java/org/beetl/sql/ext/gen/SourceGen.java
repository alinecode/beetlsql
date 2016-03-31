package org.beetl.sql.ext.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public static GroupTemplate gt = null;
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
		srcHead+="import java.util.Date;"+CR;
		srcHead+="import java.sql.Timestamp;"+CR;
		

		
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
		final TableDesc  tableDesc = mm.getTable(table);
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
			attr.put("desc", desc);
			attrs.add(attr);
		}
		
		// 主键总是拍在前面，int类型也排在前面，剩下的按照字母顺序排
		Collections.sort(attrs,new Comparator<Map>() {

			@Override
			public int compare(Map o1, Map o2) {
				ColDesc desc1  = (ColDesc)o1.get("desc");
				ColDesc desc2  = (ColDesc)o2.get("desc");
				int score1 = score(desc1);
				int score2 = score(desc2);
				if(score1==score2){
					return desc1.colName.compareTo(desc2.colName);
				}else{
					return score2-score1;
				}
				
					
			}
			
			private int score(ColDesc desc){
				if(tableDesc.getMetaIdName()!=null&&tableDesc.getMetaIdName().equalsIgnoreCase(desc.colName)){
					return 99;
				}else if(JavaType.isInteger(desc.sqlType)){
					return 9;
				}else if(JavaType.isDateType(desc.sqlType)){
					return  -9;
				}else{
					return  0;
				}
			}

			
		});
		
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

