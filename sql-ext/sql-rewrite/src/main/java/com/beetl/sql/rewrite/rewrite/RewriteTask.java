package com.beetl.sql.rewrite.rewrite;

import com.beetl.sql.rewrite.ColRewriteParam;
import com.beetl.sql.rewrite.SqlParserRewrite;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract  class  RewriteTask {
	Set<Table> table = new HashSet<>();

	SqlParserRewrite sqlParserRewrite;
	public abstract  void rewrite();
	public void addTable(Table tableName){
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
				Expression conditionExpress = null;
				if(value instanceof Number){
					Expression valueExpress = new LongValue(((Number)value).longValue());;
					if(colRewriteParam.isEqualsFlag()){
						conditionExpress = new EqualsTo(column, valueExpress);
					}else{
						conditionExpress = new NotEqualsTo(column, valueExpress);
					}

				}else if ( value instanceof  List){
					if(((List)value).isEmpty()){
						continue;
					}
					ExpressionList valueExpressList = new  ExpressionList();
					for(Object o:(List)value){
						Expression valueExpress = new LongValue(((Number)o).longValue());
						valueExpressList.addExpressions(valueExpress);
					}
					conditionExpress = new InExpression(column, valueExpressList);
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
