package com.beetl.sql.rewrite;

import com.beetl.sql.rewrite.rewrite.DeleteRewriteTask;
import com.beetl.sql.rewrite.rewrite.RewriteTask;
import com.beetl.sql.rewrite.rewrite.SelectRewriteTask;
import com.beetl.sql.rewrite.rewrite.UpdateRewriteTask;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.*;

public class SqlParserRewrite extends TablesNamesFinder {


	protected  Stack<RewriteTask> selectStack = new Stack<>();
	protected List<ColRewriteParam> colRewriteParamList;

	TableConfig tableCheck;


	public SqlParserRewrite(TableConfig tableCheck,List<ColRewriteParam> colRewriteParamList){
		this.tableCheck = tableCheck;
		this.colRewriteParamList = colRewriteParamList;
	}


	@Override
	public void visit(PlainSelect plainSelect) {
		selectStack.push(new SelectRewriteTask(plainSelect,this));
		super.visit(plainSelect);
		rewrite();
		selectStack.pop();
	}


	@Override
	public void visit(Delete delete) {
		selectStack.push(new DeleteRewriteTask(delete,this));
		super.visit(delete);
		rewrite();
		selectStack.pop();
	}


	@Override
	public void visit(Update update) {
		selectStack.push(new UpdateRewriteTask(update,this));
		super.visit(update);
		rewrite();
		selectStack.pop();
	}


	protected void rewrite() {
		RewriteTask nodeTask = selectStack.peek();
		nodeTask.rewrite();

	}

	@Override
	protected String extractTableName(Table table) {
		String name = table.getFullyQualifiedName();
		RewriteTask selectNode = selectStack.peek();
		selectNode.addTable(table);
		return name;
	}

	public List<ColRewriteParam> getColRewriteParamList() {
		return colRewriteParamList;
	}

	public void setColRewriteParamList(List<ColRewriteParam> colRewriteParamList) {
		this.colRewriteParamList = colRewriteParamList;
	}

	public TableConfig getTableCheck() {
		return tableCheck;
	}

	public void setTableCheck(TableConfig tableCheck) {
		this.tableCheck = tableCheck;
	}

	public static void main(String[] args)  throws Exception{


	}



}

