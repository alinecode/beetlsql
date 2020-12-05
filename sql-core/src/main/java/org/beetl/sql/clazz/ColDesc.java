package org.beetl.sql.clazz;

import lombok.Data;

/**
 *  列描述
 * @author xiandafu
 *
 */
@Data
public class ColDesc {
	private String colName;
	private int sqlType;
	private Integer size;
	private Integer digit;
	private String remark ;
	//YES,NO or emptyString
	private String isNullable;
	private boolean isAuto = false;
	public ColDesc(String colName, int sqlType, Integer size, Integer digit, String remark,String isNullable){
		this.colName = colName;
		this.sqlType = sqlType;
		this.size = size;
		this.digit = digit;
		this.remark = remark;
		this.isNullable = isNullable;
		
	}
	public ColDesc(String colName){
		this.colName = colName;
	}

}
