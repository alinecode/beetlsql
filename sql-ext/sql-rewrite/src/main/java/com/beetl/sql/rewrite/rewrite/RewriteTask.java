package com.beetl.sql.rewrite.rewrite;

import com.beetl.sql.rewrite.ColRewriteParam;
import com.beetl.sql.rewrite.SqlParserRewrite;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract  class  RewriteTask {
	Set<Table> tables = new HashSet<>();

	SqlParserRewrite sqlParserRewrite;
	public abstract  void rewrite();
	public void addTable(Table tableName){
		tables.add(tableName);
	}

	void setSqlRewrite(SqlParserRewrite sqlParserRewrite){
		this.sqlParserRewrite = sqlParserRewrite;
	}

	protected Expression buildWherePart(Expression oldPart){
		for(Table table: tables){
			//TODO t.getFullyQualifiedName() ?
			List<ColRewriteParam> colRewriteParams = isRewrite(table.getName());

			if(colRewriteParams.isEmpty()){
				continue;
			}
			String prefix = table.getAlias()!=null?table.getAlias().getName():table.getName();
			for(ColRewriteParam colRewriteParam : colRewriteParams){
				Column column = new Column(prefix + "."+ colRewriteParam.getCol());
				Object value = colRewriteParam.getColValueProvider().getCurrentValue();
				if(value==null){
					continue;
				}
				Expression conditionExpress = null;
				if(value instanceof Number){
					Expression valueExpress = new LongValue(((Number)value).longValue());;
					if(colRewriteParam.isEqualsFlag()){
						conditionExpress = new EqualsTo(column, valueExpress);
					}else{
						conditionExpress = new NotEqualsTo(column, valueExpress);
					}

				}else if(value instanceof CharSequence){
					Expression valueExpress = new StringValue(value.toString());;
					if(colRewriteParam.isEqualsFlag()){
						conditionExpress = new EqualsTo(column, valueExpress);
					}else{
						conditionExpress = new NotEqualsTo(column, valueExpress);
					}

				}
				else if ( value instanceof  List){
					if(((List)value).isEmpty()){
						continue;
					}
					ParenthesedExpressionList parenthesedExpressionList = new ParenthesedExpressionList();
					for(Object o:(List)value){
						Expression valueExpress = new LongValue(((Number)o).longValue());
						parenthesedExpressionList.addExpressions(valueExpress);
					}

					conditionExpress = new InExpression(column, parenthesedExpressionList);
					((InExpression)conditionExpress).setNot(!colRewriteParam.isEqualsFlag());
				}else{
					throw new UnsupportedOperationException(value!=null?value.getClass().getName():"null");
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
		for(ColRewriteParam colRewriteParam : sqlParserRewrite.getColRewriteParamList()){
			String col = colRewriteParam.getCol();
			if(sqlParserRewrite.getTableCheck().contain(table,col)){
				list.add(colRewriteParam) ;
			}
		}
		return list;
	}





}
