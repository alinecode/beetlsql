package com.beetl.sql.tenant;

import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
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

	public static abstract  class  RewriteTask {
		Set<Table> table = new HashSet<>();

		SqlParserRewrite sqlParserRewrite;
		abstract  void rewrite();
		void addTable(Table tableName){
			table.add(tableName);
		}

		void setSqlRewrite(SqlParserRewrite sqlParserRewrite){
			this.sqlParserRewrite = sqlParserRewrite;
		}

		protected Expression buildWherePart(Expression oldPart){
			for(Table t:table){
				//TODO t.getFullyQualifiedName() ?
				List<ColRewriteParam> colRewriteParams = isRewrite(t.getName());

				if(colRewriteParams.isEmpty()){
					continue;
				}
				String prefix = t.getAlias()!=null?t.getAlias().getName():t.getName();
				for(ColRewriteParam colRewriteParam : colRewriteParams){
					Column column = new Column(prefix + "."+ colRewriteParam.getCol());
					Object value = colRewriteParam.getColValueProvider().getCurrentValue();
					if(value==null){
						continue;
					}
					Expression valueExpress = null;
					if(value instanceof Number){
						valueExpress= new LongValue(((Number)value).longValue());
					}else {
						// 比如数据权限
						throw new UnsupportedOperationException("todo in ()");
					}
					Expression conditionExpress = null;
					if(colRewriteParam.isEqualsFlag()){
						conditionExpress = new EqualsTo(column, valueExpress);
					}else{
						conditionExpress = new NotEqualsTo(column, valueExpress);
					}

					if(oldPart==null){
						oldPart = conditionExpress;
					}else{
						AndExpression andExpression = new AndExpression(oldPart,conditionExpress);
						oldPart = andExpression;
					}

				}

			}
			return oldPart;

		}

		protected  List<ColRewriteParam> isRewrite(String table){
			List list = new ArrayList(2);
			for(ColRewriteParam colRewriteParam : sqlParserRewrite.colRewriteParamList){
				String col = colRewriteParam.getCol();
				if(sqlParserRewrite.tableCheck.contain(table,col)){
					list.add(colRewriteParam) ;
				}
			}
			return list;
		}



	}



	@Data
	public static class SelectRewriteTask extends   RewriteTask{
		PlainSelect plainSelect;


		public SelectRewriteTask(PlainSelect plainSelect, SqlParserRewrite sqlParserRewrite) {
			this.plainSelect = plainSelect;
			this.setSqlRewrite(sqlParserRewrite);
		}

		@Override
		public void rewrite() {
			if (table.isEmpty()) {
				return;
			}
			Expression expression = buildWherePart(plainSelect.getWhere());
			plainSelect.setWhere(expression);
		}

	}

	@Data
	public static class DeleteRewriteTask extends   RewriteTask {
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

	@Data
	public static class UpdateRewriteTask extends   RewriteTask {
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




	public static void main(String[] args)  throws Exception{

		TestTableConfig tableCheck1 = new TestTableConfig();
		ColRewriteParam tenantRewrite = new ColRewriteParam("tenant_id", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return 1;
			}
		});

		ColRewriteParam logicDeleteRewrite = new ColRewriteParam("is_delete", new ColValueProvider() {
			@Override
			public Object getCurrentValue() {
				return 0;
			}
		});


		String sql = "delete  from user u  where name=1 or cc =2";
		Statement statement = (Statement) CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));;

		SqlParserRewrite finder = new SqlParserRewrite(tableCheck1, Arrays.asList(tenantRewrite,logicDeleteRewrite));
		List<String> tables =  finder.getTableList(statement);
		System.out.println(tables);
		System.out.println(statement);
	}

	public static  class TestTableConfig implements TableConfig {

		@Override
		public boolean contain(String table, String col) {
			return col.equals("tenant_id")||col.equals("is_delete");
		}
	}



}

