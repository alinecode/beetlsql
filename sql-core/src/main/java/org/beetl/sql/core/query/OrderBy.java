package org.beetl.sql.core.query;

public class OrderBy {
	StringBuilder sb = new StringBuilder(" ORDER BY ");

	public void add(String orderBy) {
		sb.append(" ").append(orderBy).append(" ,");
	}

	public String getOrderBy() {
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public OrderBy(){
	}
	protected OrderBy(String  sql){
		this.sb = new StringBuilder(sql);
	}
}
