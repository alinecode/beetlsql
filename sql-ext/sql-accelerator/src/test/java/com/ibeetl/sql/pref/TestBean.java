package com.ibeetl.sql.pref;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TestBean {

	protected Integer col1;
	protected int col2;
	protected double col3;
	protected byte col4;
	protected long col5;
	protected short col6;
	protected String col7;
	protected Integer col8;

}
