package com.beetl.sql.rewrite;

import lombok.Data;

@Data
public class ColRewriteParam {
	private String col;
	private ColValueProvider colValueProvider;
	private boolean equalsFlag = true;
	public ColRewriteParam(String col,ColValueProvider colValueProvider) {
		this.col = col;
		this.colValueProvider = colValueProvider;
	}

	public ColRewriteParam(String col,long value) {
		this.col = col;
		this.colValueProvider = new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return value;
			}
		};
	}

	public ColRewriteParam(String col,ColValueProvider colValueProvider,boolean equals) {
		this.col = col;
		this.colValueProvider = colValueProvider;
		this.equalsFlag = equals;
	}



}
