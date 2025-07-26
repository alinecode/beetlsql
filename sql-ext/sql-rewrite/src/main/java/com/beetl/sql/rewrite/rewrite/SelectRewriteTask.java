package com.beetl.sql.rewrite.rewrite;

import com.beetl.sql.rewrite.SqlParserRewrite;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;

@Data
public  class SelectRewriteTask extends   RewriteTask{
	PlainSelect plainSelect;


	public SelectRewriteTask(PlainSelect plainSelect, SqlParserRewrite sqlParserRewrite) {
		this.plainSelect = plainSelect;
		this.setSqlRewrite(sqlParserRewrite);
	}

	@Override
	public void rewrite() {
		if (tables.isEmpty()) {
			return;
		}
		Expression expression = buildWherePart(plainSelect.getWhere());
		plainSelect.setWhere(expression);
	}

}
