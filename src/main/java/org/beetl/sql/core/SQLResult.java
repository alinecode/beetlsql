package org.beetl.sql.core;

import java.util.List;

import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.orm.MappingEntity;

 public class SQLResult {
	public String jdbcSql;
	public List<SQLParameter> jdbcPara;
	public List<MappingEntity> mapingEntrys;
}