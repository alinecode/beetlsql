package org.beetl.sql.core;

import java.util.List;

import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.orm.MappingEntity;

 public class SQLResult {
	 /**
	  * jdbc 对应的sql
	  */
	public String jdbcSql;
	/**
	 * jdbc对应的参数，包含了值，可能的对应的表达式
	 */
	public List<SQLParameter> jdbcPara;
	/**
	 * 获取jdbc对应的参数
	 * @return
	 */
	public Object[] toObjectArray(){
		if(jdbcPara==null){
			return new Object[0];
		}else {
			Object[] objs = new Object[jdbcPara.size()];
			int i =0;
			for(SQLParameter spa:jdbcPara){
				objs[i++] = spa.value;
			}
			return objs;
		}
	}

	private List<SQLResultListener> listener;

	 public List<SQLResultListener> getListener() {
		 return listener;
	 }

	 public void setListener(List<SQLResultListener> listener) {
		 this.listener = listener;
	 }
 }