package org.beetl.sql.core.query;

public class GroupBy {
	StringBuilder sb = new StringBuilder("GROUP BY ");
	boolean start = true;

	public void add(String col) {
		if (start) {
			sb.append(col).append(" ");
			start = false;
		} else {
			sb.append(",").append(col).append(" ");
		}

	}

	public String getGroupBy() {
		return sb.toString().trim();
	}

	public GroupBy addHaving(String sql) {
		sb.append("HAVING ").append(sql).append(" ");
		return this;
	}

	public GroupBy(){

	}
	public GroupBy(String str){
		sb = new StringBuilder(str);
	}


}
