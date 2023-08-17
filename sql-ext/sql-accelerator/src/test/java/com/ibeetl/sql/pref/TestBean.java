package com.ibeetl.sql.pref;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestBean {

	private Integer col1;
	private String col2;
	private String col3;
	private String col4;
	private String col5;
	private String col6;
	private BigDecimal col7;
	private Integer col8;

	public Integer getCol1() {
		if(1==1) throw new RuntimeException("ex");
		return col1;
	}

	public void setCol1(Integer col1) {
		if(1==1) throw new RuntimeException("ex");
		this.col1 = col1;
	}
}
