package com.beetl.sql.rewrite;

import lombok.Data;

@Data
public class TableRewriteParam {
	private String name;
	private TableNameProvider tableNameProvider;
	public TableRewriteParam(String name,TableNameProvider tableNameProvider) {
		this.name = name;
		this.tableNameProvider = tableNameProvider;
	}
}
