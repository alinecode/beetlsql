package org.beetl.sql.core.db;

/**
 *  列描述
 * @author xiandafu
 *
 */
public class ColDesc {
	public String colName;
	public int sqlType;
	public Integer size;
	public Integer digit;
	public String remark ;
	public ColDesc(String colName,int sqlType,Integer size,Integer digit,String remark){
		this.colName = colName;
		this.sqlType = sqlType;
		this.size = size;
		this.digit = digit;
		this.remark = remark;
		
	}
}
