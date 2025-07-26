package com.beetl.sql.rewrite.rewrite;


import com.beetl.sql.rewrite.ColRewriteParam;
import com.beetl.sql.rewrite.SqlParserRewrite;
import com.beetl.sql.rewrite.TableRewriteParam;
import lombok.Data;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Values;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.List;

@Data
public  class InsertRewriteTask extends   RewriteTask {
	Insert insert;

	public InsertRewriteTask(Insert insert, SqlParserRewrite sqlParserRewrite) {
		this.insert = insert;
		this.setSqlRewrite(sqlParserRewrite);
	}

	@Override
	public void rewrite() {
		Table table = insert.getTable();
		String prefix = table.getAlias()!=null?table.getAlias().getName():table.getName();

		TableRewriteParam tableRewriteParam = sqlParserRewrite.getTableRewriteParam();
		String tableName = table.getName();


		if(sqlParserRewrite.getColRewriteParamList().isEmpty()){
			return ;
		}

		for(ColRewriteParam colRewriteParam : sqlParserRewrite.getColRewriteParamList()){
			String col = colRewriteParam.getCol();
			if(sqlParserRewrite.getTableCheck().contain(tableName,col)){
				Column column = new Column(colRewriteParam.getCol());
				insert.getColumns().add(column);
				Object value = colRewriteParam.getColValueProvider().getCurrentValue();
				if(value==null){
					continue;
				}
				if(value instanceof Number){
					Expression valueExpress = new LongValue(((Number)value).longValue());;
					addInsertExp(insert.getValues(),valueExpress);

				}else if(value instanceof CharSequence){
					StringValue stringExpress = new StringValue(value.toString());
					addInsertExp(insert.getValues(),stringExpress);
				}
				else{
					throw new UnsupportedOperationException("不支持类型 column="+column+","+value.getClass());
				}
			}
		}



	}

	protected void addInsertExp(Values value,Expression expression){
		ExpressionList<?> list = value.getExpressions();
		if(list instanceof ParenthesedExpressionList){
			value.addExpressions(expression);
			return ;
		}
		//只有一列 insert into user (name) values (?)，特殊处理

		Parenthesis parenthesis = (Parenthesis)value.getExpressions().get(0); // (?)
		ParenthesedExpressionList parenthesedExpressionList = new ParenthesedExpressionList();
		parenthesedExpressionList.add(parenthesis.getExpression());
		parenthesedExpressionList.add(expression);
		value.setExpressions(parenthesedExpressionList);
	}


}
