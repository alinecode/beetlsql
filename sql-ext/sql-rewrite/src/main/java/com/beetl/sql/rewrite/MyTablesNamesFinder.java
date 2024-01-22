package com.beetl.sql.rewrite;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.List;

public class MyTablesNamesFinder extends TablesNamesFinder {

	/**
	 * 从4.1版本过度到4.7，貌似join部分有问题
	 * @param plainSelect
	 */
	@Override
	public void visit(PlainSelect plainSelect) {
		List<WithItem> withItemsList = plainSelect.getWithItemsList();
		if (withItemsList != null && !withItemsList.isEmpty()) {
			for (WithItem withItem : withItemsList) {
				withItem.accept((SelectVisitor) this);
			}
		}
		if (plainSelect.getSelectItems() != null) {
			for (SelectItem<?> item : plainSelect.getSelectItems()) {
				item.accept(this);
			}
		}

		if (plainSelect.getFromItem() != null) {
			plainSelect.getFromItem().accept(this);
		}

		if (plainSelect.getJoins() != null) {
			for (Join join : plainSelect.getJoins()) {
				FromItem fromItem = join.getFromItem();
				FromItem rightItem = join.getRightItem();
				if(fromItem==rightItem){
					// 感觉4.7，4.8 实现有问题，俩个都一样，为什么要遍历俩次
					join.getRightItem().accept(this);
				}else{
					join.getFromItem().accept(this);
					join.getRightItem().accept(this);
				}


				for (Expression expression : join.getOnExpressions()) {
					expression.accept(this);
				}
			}
		}
		if (plainSelect.getWhere() != null) {
			plainSelect.getWhere().accept(this);
		}

		if (plainSelect.getHaving() != null) {
			plainSelect.getHaving().accept(this);
		}

		if (plainSelect.getOracleHierarchical() != null) {
			plainSelect.getOracleHierarchical().accept(this);
		}
	}
}
