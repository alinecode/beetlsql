package org.beetl.sql.core;

import org.beetl.sql.core.engine.SQLParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorContext {

	private Map<String, Object> env = null;

	private ExecuteContext executeContext;

	public InterceptorContext(ExecuteContext executeContext) {
		this.executeContext = executeContext;
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

	public Map<String, Object> getEnv() {
		return env;
	}

	public void setEnv(Map<String, Object> env) {
		this.env = env;
	}

	public ExecuteContext getExecuteContext() {
		return executeContext;
	}

	public void setExecuteContext(ExecuteContext executeContext) {
		this.executeContext = executeContext;
	}
}
