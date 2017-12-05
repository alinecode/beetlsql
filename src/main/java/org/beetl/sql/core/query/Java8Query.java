package org.beetl.sql.core.query;

import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.beetl.sql.core.SQLManager;

import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.InvocationExpression;
import com.trigersoft.jaque.expression.LambdaExpression;
import com.trigersoft.jaque.expression.MemberExpression;

public class Java8Query<T> extends Query<T> {

	public Java8Query(SQLManager sqlManager, Class clazz) {
		super(sqlManager, clazz);
		// TODO Auto-generated constructor stub
	}

	public List<T> select(Property<T, ?>... cols) {
		String[] colArray = this.getFunctionName(cols);
		return super.select(colArray);

	}

	public Java8Query<T> andEq(Property<T, ?> fun, Object value) {
		super.andEq(getFunctionName(fun), value);
		return this;
	}

	public Java8Query<T> andNotEq(Property<T, ?> fun, Object value) {
		super.andNotEq(getFunctionName(fun), value);
		return this;

	}

	public Java8Query<T> andGreat(Property<T, ?> fun, Object value) {
		super.appendAndSql(getFunctionName(fun), value, ">");
		return this;

	}

	public Java8Query<T> andGreatEq(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, ">=");
		return this;
	}

	public Java8Query<T> andLess(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, "<");
		return this;
	}

	public Java8Query<T> andLessEq(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, "<=");
		return this;
	}

	public Java8Query<T> andLike(Property<T, ?> fun, String value) {
		appendAndSql(getFunctionName(fun), value, "LIKE ");
		return this;
	}

	public Java8Query<T> andNotLike(Property<T, ?> fun, String value) {
		appendAndSql(getFunctionName(fun), value, "NOT LIKE ");
		return this;
	}

	public Java8Query<T> andIsNull(Property<T, ?> fun) {
		appendAndSql(getFunctionName(fun), null, "IS NULL ");
		return this;
	}

	public Java8Query<T> andIsNotNull(Property<T, ?> fun) {
		appendAndSql(getFunctionName(fun), null, "IS NOT NULL ");
		return this;
	}

	public Java8Query<T> andIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, IN, AND);
		return this;
	}

	public Java8Query<T> andNotIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, NOT_IN, AND);
		return this;
	}

	public Java8Query<T> andBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), BETWEEN, AND, value1, value2);
		return this;
	}

	public Java8Query<T> andNotBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), NOT_BETWEEN, AND, value1, value2);
		return this;
	}

	public Java8Query<T> orEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "=");
		return this;
	}

	public Java8Query<T> orNotEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<>");
		return this;
	}

	public Java8Query<T> orGreat(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, ">");
		return this;
	}

	public Java8Query<T> orGreatEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, ">=");
		return this;
	}

	public Java8Query<T> orLess(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<");
		return this;
	}

	public Java8Query<T> orLessEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<=");
		return this;
	}

	public Java8Query<T> orLike(Property<T, ?> fun, String value) {
		appendOrSql(getFunctionName(fun), value, "LIKE");
		return this;
	}

	public Java8Query<T> orNotLike(Property<T, ?> fun, String value) {
		appendOrSql(getFunctionName(fun), value, "NOT LIKE");
		return this;
	}

	public Java8Query<T> orIsNull(Property<T, ?> fun) {
		appendOrSql(getFunctionName(fun), null, "IS NULL");
		return this;
	}

	public Java8Query<T> orIsNotNull(Property<T, ?> fun) {
		appendOrSql(getFunctionName(fun), null, "IS NOT NULL");
		return this;
	}

	public Java8Query<T> orIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, IN, OR);
		return this;
	}

	public Java8Query<T> orNotIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, NOT_IN, OR);
		return this;
	}

	public Java8Query<T> orBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), BETWEEN, OR, value1, value2);
		return this;
	}

	public Java8Query<T> orNotBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), NOT_BETWEEN, OR, value1, value2);
		return this;
	}
	
	    public Java8Query<T> groupBy(Property<T, ?> fun) {
	    		super.groupBy(getFunctionName(fun));
	      
	        return this;
	    }

	    public Java8Query<T> orderBy(Property<T, ?> fun) {
	    		super.orderBy(getFunctionName(fun));
	        return this;
	    }

	private String getFunctionName(Property<T, ?> fun) {
		LambdaExpression<Property<T, ?>> parsed = LambdaExpression.parse(fun);
		Expression body = parsed.getBody();
		Member member = ((MemberExpression) ((InvocationExpression) body).getTarget()).getMember();
		String method = member.getName();
		String attr = null;
		// @TODO,getter到属性
		if (method.startsWith("get")) {
			attr = method.substring(3);
		} else {
			attr = method.substring(2);
		}
		return sqlManager.getNc().getColName(clazz, attr);
	}

	private String[] getFunctionName(Property<T, ?>... funs) {
		String[] cols = new String[funs.length];
		int i = 0;
		for (Property<T, ?> fun : funs) {
			cols[i++] = this.getFunctionName(fun);
		}
		return cols;

	}

	public interface Property<T, R> extends Function<T, R>, Serializable {
	}

}
