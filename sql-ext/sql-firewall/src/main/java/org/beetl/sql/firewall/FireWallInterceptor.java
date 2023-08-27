package org.beetl.sql.firewall;

import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SqlId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FireWallInterceptor implements Interceptor {

	FireWall fireWall;
	Logger logger = LoggerFactory.getLogger(FireWallInterceptor.class);
	public FireWallInterceptor(FireWall fireWall){
		this.fireWall = fireWall;
	}
	@Override
	public void before(InterceptorContext ctx) {
		SqlId sqlId =ctx.getExecuteContext().sqlId;
		if(fireWall.getWhiteList().contains(sqlId)){
			return ;
		}
		String sql = ctx.getExecuteContext().sqlResult.jdbcSql;
		if(fireWall.getSqlMaxLength()!=0){
			if(sql.length()>fireWall.getSqlMaxLength()){
				action("exceed "+sql+" expected less than "+fireWall.getSqlMaxLength());
			}
		}
		Statement statement = null;
		if(ctx.getExecuteContext().sqlId.isPage()){
			//跳过sql解析
			return ;
		}
		try {
			 statement  = CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));

		} catch (JSQLParserException e) {
			logger.error("parser  [ "+sql+" ]  error "+e.getMessage());
			return;
		}
		if(statement instanceof Update){
			checkUpdate((Update)statement);
		}else if(statement instanceof Delete){
			checkDelete((Delete) statement);
		}else if(statement instanceof Truncate){
			if(!fireWall.isTruncateEnable()){
				action("truncate table error  "+statement.toString());
			}
		}
		else if(statement instanceof CreateTable){
			if(!fireWall.isDmlCreateEnable()){
				action("create table error  "+statement.toString());
			}
		}else if(statement instanceof Drop){
			if(!fireWall.isDmlDropEnable()){
				action("drop table error  "+statement.toString());
			}
		}else if(statement instanceof Alter){
			if(!fireWall.isDmlAlterEnable()){
				action("alter table error  "+statement.toString());
			}
		}
	}




	protected  void checkUpdate(Update update){
		if(fireWall.isUpdateUnLimit()){
			return ;
		}
		Expression where = update.getWhere();
		if(where==null){
			action("update unlimit "+update.toString());
		}
		boolean isAlwayTrue = isConstantExpression(where);
		if(isAlwayTrue){
			action("update unlimit "+update.toString());
		}

		return ;

	}

	protected  void checkDelete(Delete delete){
		Expression where = delete.getWhere();
		if(where==null){
			action("delete unlimit "+delete.toString());
		}
		boolean isAlwayTrue = isConstantExpression(where);
		if(isAlwayTrue){
			action("delete unlimit "+delete.toString());
		}


		return ;
	}

	protected  void action(String  msg){
		if(fireWall.getAction()==1){
			logger.warn(msg);
		}else{
			throw new BeetlSQLException(BeetlSQLException.SQL_EXCEPTION,"FIREWALL:"+msg);
		}

	}


	@Override
	public void after(InterceptorContext ctx) {
		//do nothing
	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {
		//do nothing
	}

	protected boolean isConstantExpression(Expression express){
		//考虑最简单的情况 如 where  1=1 ，对于 where 1=1 and 2 = 2 等复杂表达式，需要考虑借些每个表达式（TODO）
		if(express instanceof EqualsTo){
			EqualsTo equalsTo = (EqualsTo)express;
			if(equalsTo.getLeftExpression().toString().equals(equalsTo.getRightExpression().toString())){
				return  true ;
			}
		}
		return false;
	}


	 public static void main(String[] args)  throws Exception{
		String sql = "delete from user  where name=1 or cc =2";
		 Statement statement = (Statement)CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));;
		 MyTableFinder finder = new MyTableFinder();
		 List<String> tables =  finder.getTableList(statement);
		 System.out.println(tables);
		 System.out.println(statement);
	}
	public  static class  MyTableFinder extends TablesNamesFinder{

		public Stack<SelectNode> selectStack = new Stack<>();
		@Override
		public void visit(PlainSelect plainSelect) {
			selectStack.push(new SelectNode(plainSelect));
			if (plainSelect.getSelectItems() != null) {
				for (SelectItem item : plainSelect.getSelectItems()) {
					item.accept(this);
				}
			}

			if (plainSelect.getFromItem() != null) {
				plainSelect.getFromItem().accept(this);
			}

			if (plainSelect.getJoins() != null) {
				for (Join join : plainSelect.getJoins()) {
					join.getRightItem().accept(this);
				}
			}
			if (plainSelect.getWhere() != null) {
				plainSelect.getWhere().accept(this);
				append(plainSelect);

			}else{
				append(plainSelect);
			}

			if (plainSelect.getHaving() != null) {
				plainSelect.getHaving().accept(this);
			}

			if (plainSelect.getOracleHierarchical() != null) {
				plainSelect.getOracleHierarchical().accept(this);
			}
			selectStack.pop();
		}

		protected  void append(PlainSelect plainSelect){
			SelectNode node = selectStack.peek();
			if(node.getTable().isEmpty()){
				return ;
			}
			for(String table: node.getTable()){
				Column column = new Column(table+".xxx");
				LongValue longValue = new LongValue(1);
				EqualsTo equalsTo = new EqualsTo(column,longValue);
				if(plainSelect.getWhere()==null){
					plainSelect.setWhere(equalsTo);
				}else{
					AndExpression andExpression = new AndExpression(plainSelect.getWhere(),equalsTo);
					plainSelect.setWhere(andExpression);
				}
			}

		}

		@Override
		protected String extractTableName(Table table) {
			String name = table.getFullyQualifiedName();
			SelectNode selectNode = selectStack.peek();
			selectNode.getTable().add(name);
			return name;
		}


	}

	@Data
	public static  class SelectNode{
		PlainSelect plainSelect;
		Set<String> table = new HashSet<>();
		public SelectNode(PlainSelect plainSelect){
			this.plainSelect = plainSelect;
		}



	}
}
