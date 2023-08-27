package com.beetl.sql.tenant.rewrite;


import com.beetl.sql.tenant.SqlParserRewrite;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.update.Update;

@Data
public  class UpdateRewriteTask extends   RewriteTask {
	Update updateSelect;

	public UpdateRewriteTask(Update updateSelect, SqlParserRewrite sqlParserRewrite) {
		this.updateSelect = updateSelect;
		this.setSqlRewrite(sqlParserRewrite);
	}

	@Override
	public void rewrite() {
		if (table.isEmpty()) {
			return;
		}
		Expression expression = buildWherePart(updateSelect.getWhere());
		updateSelect.setWhere(expression);
	}


}
