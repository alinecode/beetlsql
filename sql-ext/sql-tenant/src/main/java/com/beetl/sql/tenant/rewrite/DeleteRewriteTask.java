package com.beetl.sql.tenant.rewrite;

import com.beetl.sql.tenant.SqlParserRewrite;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.delete.Delete;

@Data
public  class DeleteRewriteTask extends   RewriteTask {
	Delete deleteSelect;

	public DeleteRewriteTask(Delete deleteSelect, SqlParserRewrite sqlParserRewrite) {
		this.deleteSelect = deleteSelect;
		this.setSqlRewrite(sqlParserRewrite);
	}

	@Override
	public void rewrite() {
		if (table.isEmpty()) {
			return;
		}
		Expression expression = buildWherePart(deleteSelect.getWhere());
		deleteSelect.setWhere(expression);
	}


}
