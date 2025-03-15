package com.beetl.sql.rewrite;

import com.beetl.sql.rewrite.rewrite.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.*;
import java.util.function.Predicate;

/**
 * 继承TablesNamesFinder，通过遍历sql ast，使用com.beetl.sql.rewrite.rewrite.* 进行重写
 *
 */
public class SqlParserRewrite extends MyTablesNamesFinder {


	protected  Stack<RewriteTask> selectStack = new Stack<>();
	protected List<ColRewriteParam> colRewriteParamList;
	protected TableRewriteParam tableRewriteParam;

	TableConfig tableCheck;

	public SqlParserRewrite(TableConfig tableCheck,List<ColRewriteParam> colRewriteParamList,TableRewriteParam tableRewriteParam){
		this.tableCheck = tableCheck;
		this.colRewriteParamList = colRewriteParamList;
		this.tableRewriteParam = tableRewriteParam;
	}

	public SqlParserRewrite(TableConfig tableCheck,List<ColRewriteParam> colRewriteParamList){
		this(tableCheck,colRewriteParamList,null);
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

	@Override
	public void visit(Insert insert) {
		selectStack.push(new InsertRewriteTask(insert,this));
		super.visit(insert);
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


	@Override
	public void visit(Table tableName) {
		super.visit(tableName);
		if(tableRewriteParam==null){
			return ;
		}
		String table = tableName.getName();
		if(!tableRewriteParam.match(table)){
			return ;
		}

		String newName = tableRewriteParam.getTableNameProvider().getTableName(table);
		tableName.setName(newName);
	}

	public TableRewriteParam getTableRewriteParam() {
		return tableRewriteParam;
	}

	public void setTableRewriteParam(TableRewriteParam tableRewriteParam) {
		this.tableRewriteParam = tableRewriteParam;
	}

	public static void main(String[] args)  throws Exception{


	}




}

