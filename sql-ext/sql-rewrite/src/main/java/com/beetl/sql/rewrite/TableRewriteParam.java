package com.beetl.sql.rewrite;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class TableRewriteParam {
	private Set<String> names;
	private TableNameProvider tableNameProvider;
	public TableRewriteParam(String name,TableNameProvider tableNameProvider) {
		this(Arrays.asList(name),tableNameProvider);

	}
	public TableRewriteParam(List<String> names, TableNameProvider tableNameProvider) {
		this.names = names.stream().map(String::toLowerCase).collect(Collectors.toSet());
		this.tableNameProvider = tableNameProvider;
	}
	public boolean match(String  name){
		return names.contains(name.toLowerCase());
	}
}
