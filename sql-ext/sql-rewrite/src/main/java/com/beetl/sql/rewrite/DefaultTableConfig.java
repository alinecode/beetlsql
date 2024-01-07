package com.beetl.sql.rewrite;

import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.core.meta.MetadataManager;

public class DefaultTableConfig implements TableConfig {
	MetadataManager metadataManager;
	public DefaultTableConfig(MetadataManager metadataManager){
		this.metadataManager  = metadataManager;
	}
	@Override
	public boolean contain(String t, String c) {
		String table = trim(t);
		if(!metadataManager.existTable(table)){
			return false;
		}
		TableDesc tableDesc = metadataManager.getTable(table);
		if(tableDesc==null){
			return false;
		}
		String col = trim(c);
		return tableDesc.getCols().contains(col);
	}
	protected String trim(String name ){
		char ch = name.charAt(0);
		//目前考虑mysql和sqlserver ,不知道还有没有别的情况
		if(ch=='`'||ch=='['){
			return  name.substring(1,name.length()-1);
		}else{
			return name;
		}
	}

}
