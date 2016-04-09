package org.beetl.sql.core;

public class SQLSource {
	
	private String id;
	private String template;
	//主键名称
	private String IdCol;
	private int idType;
	private int line = 0;
	public SQLSource() {
	}

	public SQLSource(String id, String template) {
		this.id = id;
		this.template = template;
	}

	public SQLSource(String template) {

		this.template = template;
	}
	
	


	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	public String getIdCol() {
		return IdCol;
	}

	public void setIdCol(String IdCol) {
		this.IdCol = IdCol;
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
