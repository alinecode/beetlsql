package org.beetl.sql.core.query;

public class GroupBy {
	StringBuilder sb = new StringBuilder("GROUP BY ");
	public void add(String col) {
		sb.append(col).append(" ,");
	}
	public String getGroupBy() {
		sb.setLength(sb.length()-1);
		return sb.toString();
	}
}
