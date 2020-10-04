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
	private boolean isAuto = false;
	public ColDesc(String colName, int sqlType, Integer size, Integer digit, String remark){
		this.colName = colName;
		this.sqlType = sqlType;
		this.size = size;
		this.digit = digit;
		this.remark = remark;
		
	}
	public ColDesc(String colName){
		this.colName = colName;
	}

}
