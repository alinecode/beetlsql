package org.beetl.sql.core.kit;

public class Constants {
	public static final int SELECT_BY_ID = 0;
	public static final int SELECT_BY_TEMPLATE = 1;
	public static final int SELECT_COUNT_BY_TEMPLATE = 2;	
	public static final int DELETE_BY_ID = 3;
	public static final int SELECT_ALL = 4;
	public static final int UPDATE_ALL = 5;
	public static final int UPDATE_BY_ID = 6;
	public static final int UPDATE_TEMPLATE_BY_ID = 7;
	
	//public static final int UPDATE_BY_ID_BATCH =7;
	public static final int INSERT = 8;
	
	public static  String[] classSQL = new String[]{"_gen_selectById","_gen_selectByTemplate","_gen_selectCountByTemplate","_gen_delById","_gen_selectAll","_gen_updateAll","_gen_updateById","_gen_updateBatchById","_gen_insert"};

}
