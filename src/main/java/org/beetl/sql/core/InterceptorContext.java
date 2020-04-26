package org.beetl.sql.core;

import org.beetl.sql.core.engine.SQLParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorContext {
	protected String sqlId;
	protected String sql;
	protected List<SQLParameter> paras;
	protected Map<String, Object> env = null;
	protected boolean isUpdate = false;
	protected Object result;
	protected Map<String, Object> inputParas;

	public InterceptorContext(String sqlId, String sql, List<SQLParameter> paras, Map<String, Object> inputParas,
			boolean isUpdate) {
		this.sql = sql;
		this.paras = paras;
		this.sqlId = sqlId;
		this.inputParas = inputParas;
		this.isUpdate = isUpdate;
	}

	public void put(String key, Object value) {
		if (env == null) {
			env = new HashMap<String, Object>();
		}
		env.put(key, value);
	}

	public Object get(String key) {
		if (env == null) {
			return null;
		} else {
			return env.get(key);
		}

	}

	public String getSql() {
		return sql;
	}
	
	

	public void setSql(String sql) {
        this.sql = sql;
    }

    public List<SQLParameter> getParas() {
		return paras;
	}

	public String getSqlId() {
		return sqlId;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Map<String, Object> getInputParas() {
		return inputParas;
	}

}
