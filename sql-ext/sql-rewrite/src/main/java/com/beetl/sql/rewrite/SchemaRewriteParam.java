package com.beetl.sql.rewrite;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * schema重写，每个租户一个数据库schema
 */
public class SchemaRewriteParam  extends  TableRewriteParam{

	public SchemaRewriteParam(TableNameProvider tableNameProvider) {
		super(Collections.emptyList(),tableNameProvider);

	}

	public boolean match(String  name){
		return true;
	}
}
