package org.beetl.sql.core.query;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.kit.StringKit;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 
 * @author xiandafu
 *
 */
public class LambdaQuery<T> extends Query<T> {

	public LambdaQuery(SQLManager sqlManager, Class clazz) {
		super(sqlManager, clazz);
	}

	public List<T> select(Property<T, ?>... cols) {
		String[] colArray = this.getFunctionName(cols);
		return super.select(colArray);

	}

	public LambdaQuery<T> andEq(Property<T, ?> fun, Object value) {
		super.andEq(getFunctionName(fun), value);
		return this;
	}

	public LambdaQuery<T> andNotEq(Property<T, ?> fun, Object value) {
		super.andNotEq(getFunctionName(fun), value);
		return this;

	}

	public LambdaQuery<T> andGreat(Property<T, ?> fun, Object value) {
		super.appendAndSql(getFunctionName(fun), value, ">");
		return this;

	}

	public LambdaQuery<T> andGreatEq(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, ">=");
		return this;
	}

	public LambdaQuery<T> andLess(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, "<");
		return this;
	}

	public LambdaQuery<T> andLessEq(Property<T, ?> fun, Object value) {
		appendAndSql(getFunctionName(fun), value, "<=");
		return this;
	}

	public LambdaQuery<T> andLike(Property<T, ?> fun, String value) {
		appendAndSql(getFunctionName(fun), value, "LIKE ");
		return this;
	}

	public LambdaQuery<T> andNotLike(Property<T, ?> fun, String value) {
		appendAndSql(getFunctionName(fun), value, "NOT LIKE ");
		return this;
	}

	public LambdaQuery<T> andIsNull(Property<T, ?> fun) {
		appendAndSql(getFunctionName(fun), null, "IS NULL ");
		return this;
	}

	public LambdaQuery<T> andIsNotNull(Property<T, ?> fun) {
		appendAndSql(getFunctionName(fun), null, "IS NOT NULL ");
		return this;
	}

	public LambdaQuery<T> andIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, IN, AND);
		return this;
	}

	public LambdaQuery<T> andNotIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, NOT_IN, AND);
		return this;
	}

	public LambdaQuery<T> andBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), BETWEEN, AND, value1, value2);
		return this;
	}

	public LambdaQuery<T> andNotBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), NOT_BETWEEN, AND, value1, value2);
		return this;
	}

	public LambdaQuery<T> orEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "=");
		return this;
	}

	public LambdaQuery<T> orNotEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<>");
		return this;
	}

	public LambdaQuery<T> orGreat(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, ">");
		return this;
	}

	public LambdaQuery<T> orGreatEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, ">=");
		return this;
	}

	public LambdaQuery<T> orLess(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<");
		return this;
	}

	public LambdaQuery<T> orLessEq(Property<T, ?> fun, Object value) {
		appendOrSql(getFunctionName(fun), value, "<=");
		return this;
	}

	public LambdaQuery<T> orLike(Property<T, ?> fun, String value) {
		appendOrSql(getFunctionName(fun), value, "LIKE");
		return this;
	}

	public LambdaQuery<T> orNotLike(Property<T, ?> fun, String value) {
		appendOrSql(getFunctionName(fun), value, "NOT LIKE");
		return this;
	}

	public LambdaQuery<T> orIsNull(Property<T, ?> fun) {
		appendOrSql(getFunctionName(fun), null, "IS NULL");
		return this;
	}

	public LambdaQuery<T> orIsNotNull(Property<T, ?> fun) {
		appendOrSql(getFunctionName(fun), null, "IS NOT NULL");
		return this;
	}

	public LambdaQuery<T> orIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, IN, OR);
		return this;
	}

	public LambdaQuery<T> orNotIn(Property<T, ?> fun, Collection<?> value) {
		appendInSql(getFunctionName(fun), value, NOT_IN, OR);
		return this;
	}

	public LambdaQuery<T> orBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), BETWEEN, OR, value1, value2);
		return this;
	}

	public LambdaQuery<T> orNotBetween(Property<T, ?> fun, Object value1, Object value2) {
		appendBetweenSql(getFunctionName(fun), NOT_BETWEEN, OR, value1, value2);
		return this;
	}

	public LambdaQuery<T> groupBy(Property<T, ?> fun) {
		super.groupBy(getFunctionName(fun));

		return this;
	}

	public LambdaQuery<T> orderBy(Property<T, ?> fun) {
		super.orderBy(getFunctionName(fun));
		return this;
	}

	public Query<T> asc(Property<T, ?> fun) {
		super.asc(getFunctionName(fun));

		return this;
	}

	public Query<T> desc(Property<T, ?> fun) {
		super.desc(getFunctionName(fun));
		return this;
	}

	private String getFunctionName(Property<T, ?> fun) {
        try {
            Method declaredMethod = fun.getClass().getDeclaredMethod("writeReplace");
            declaredMethod.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) declaredMethod.invoke(fun);
            String method = serializedLambda.getImplMethodName();
            String attr = null;
            if (method.startsWith("get")) {
                attr = method.substring(3);
            } else {
                attr = method.substring(2);
            }
            return sqlManager.getNc().getColName(clazz, StringKit.toLowerCaseFirstOne(attr));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

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


    //下面是重写父类一些方法，将返回值修改为LambdaQuery，这样方便继续链式lambda调用

    @Override
    public LambdaQuery<T> condition() {
        return new LambdaQuery(this.sqlManager, clazz);
    }

    @Override
    public LambdaQuery<T> and(QueryCondition condition) {
        super.and(condition);
        return this;
    }

    @Override
    public LambdaQuery<T> or(QueryCondition condition) {
        super.or(condition);
        return this;
    }

    @Override
    public LambdaQuery<T> having(QueryCondition condition) {
        super.having(condition);
        return this;
    }

    @Override
    public LambdaQuery<T> limit(long startRow, long pageSize) {
        super.limit(startRow, pageSize);
        return this;
    }

    @Override
    public LambdaQuery<T> addParam(Collection<?> objects) {
        super.addParam(objects);
        return this;
    }


    @Override
    public LambdaQuery<T> appendSql(String sqlPart) {
        super.appendSql(sqlPart);
        return this;
    }


    @Override
    public LambdaQuery<T> addPreParam(List<Object> objects) {
        super.addPreParam(objects);
        return this;
    }

    @Override
    public LambdaQuery<T> addParam(Object object) {
        super.addParam(object);
        return this;
    }

	//下面所有的方法是将父类的方法重写一次并标注为废弃，这样防止在链式调用中调用到父类方法导致无法继续使用lambda


    @Override
    @Deprecated
    public Query<T> groupBy(String column) {
        return super.groupBy(column);
    }

    @Override
    @Deprecated
    public Query<T> orderBy(String orderBy) {
        return super.orderBy(orderBy);
    }

    @Override
    @Deprecated
    public Query<T> asc(String column) {
        return super.asc(column);
    }

    @Override
    @Deprecated
    public Query<T> desc(String column) {
        return super.desc(column);
    }

    @Override
    @Deprecated
    public Query<T> andEq(String column, Object value) {
        return super.andEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andNotEq(String column, Object value) {
        return super.andNotEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andGreat(String column, Object value) {
        return super.andGreat(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andGreatEq(String column, Object value) {
        return super.andGreatEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andLess(String column, Object value) {
        return super.andLess(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andLessEq(String column, Object value) {
        return super.andLessEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andLike(String column, String value) {
        return super.andLike(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andNotLike(String column, String value) {
        return super.andNotLike(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andIsNull(String column) {
        return super.andIsNull(column);
    }

    @Override
    @Deprecated
    public Query<T> andIsNotNull(String column) {
        return super.andIsNotNull(column);
    }

    @Override
    @Deprecated
    public Query<T> andIn(String column, Collection<?> value) {
        return super.andIn(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andNotIn(String column, Collection<?> value) {
        return super.andNotIn(column, value);
    }

    @Override
    @Deprecated
    public Query<T> andBetween(String column, Object value1, Object value2) {
        return super.andBetween(column, value1, value2);
    }

    @Override
    @Deprecated
    public Query<T> andNotBetween(String column, Object value1, Object value2) {
        return super.andNotBetween(column, value1, value2);
    }

    @Override
    public Query<T> orEq(String column, Object value) {
        return super.orEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orNotEq(String column, Object value) {
        return super.orNotEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orGreat(String column, Object value) {
        return super.orGreat(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orGreatEq(String column, Object value) {
        return super.orGreatEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orLess(String column, Object value) {
        return super.orLess(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orLessEq(String column, Object value) {
        return super.orLessEq(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orLike(String column, String value) {
        return super.orLike(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orNotLike(String column, String value) {
        return super.orNotLike(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orIsNull(String column) {
        return super.orIsNull(column);
    }

    @Override
    @Deprecated
    public Query<T> orIsNotNull(String column) {
        return super.orIsNotNull(column);
    }

    @Override
    @Deprecated
    public Query<T> orIn(String column, Collection<?> value) {
        return super.orIn(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orNotIn(String column, Collection<?> value) {
        return super.orNotIn(column, value);
    }

    @Override
    @Deprecated
    public Query<T> orBetween(String column, Object value1, Object value2) {
        return super.orBetween(column, value1, value2);
    }

    @Override
    @Deprecated
    public Query<T> orNotBetween(String column, Object value1, Object value2) {
        return super.orNotBetween(column, value1, value2);
    }
}
