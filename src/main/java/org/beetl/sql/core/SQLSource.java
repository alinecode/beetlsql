package org.beetl.sql.core;

import org.beetl.sql.core.ClasspathLoader.SQLFileVersion;

public class SQLSource {
	
	protected String id;
	protected String template;
	protected int line = 0;
	SQLFileVersion version = new SQLFileVersion() ;
	
	public SQLSource() {
	}

	public SQLSource(String id, String template) {
		this.id = id;
		this.template = template;
	}

	
	
	


	public SQLFileVersion getVersion() {
		return version;
	}

	public void setVersion(SQLFileVersion version) {
		this.version = version;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	

	
}
