package org.beetl.sql.core.call;

import java.util.List;

public class CallParser {
	String template;
	public CallParser(String template){
		this.template = template;
	}
	public void parse(){

	}
	public String getJdbcSQL(){
		return null;
	}

	public List<CallParam> getCallParam(){
		return null;
	}


}
