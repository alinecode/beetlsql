package org.beetl.sql.core.db;

import java.util.HashSet;
import java.util.Set;

class TableDesc{
	//保持大写
	public String name;
	// 默认为id，列明采用小写
	public String idName="id";
	// 采用小写
	public Set<String> cols = new HashSet<String>();
}