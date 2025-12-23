package org.beetl.sql.core.query;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.interfacer.StrongValue;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 用于方便的构造简单的sql操作，复杂的sql操作建议直接使用sql语句
 * <pre>@{code
 *     LambdaQuery<User> query = ... ;
 *     List<User> list = query.andEq(User::getName,"lijz").select();
 *
 * }</pre>
 * LambdaQuery 相对于Query，够能方便支持重构，通过User::getName这种形式获取列名。
 * @author GavinKing
 * @author lijiazhi
 */
public class LambdaQuery<T> extends Query<T> {

    public LambdaQuery(SQLManager sqlManager, Class clazz) {
        super(sqlManager, clazz);
    }

    @Override
    public List<T> select() {
        return super.select();
    }

    public List<T> select(Property<T, ?>... cols) {
        String[] colArray = this.getColumnNames(cols);
        return super.select(colArray);

    }

    public PageResult<T> page(long pageNumber, long pageSize) {
        return super.page(pageNumber, pageSize);
    }

    public PageResult<T> page(long pageNumber, long pageSize, Property<T, ?>... cols) {
        String[] colArray = this.getColumnNames(cols);
        return super.page(pageNumber, pageSize, colArray);
    }

    public LambdaQuery<T> andEq(Property<T, ?> property, Object value) {
        super.andEq(getColumnName(property), value);
        return this;
    }

    public LambdaQuery<T> andNotEq(Property<T, ?> property, Object value) {
        super.andNotEq(getColumnName(property), value);
        return this;

    }

    /**
     * 大于
     */
    public LambdaQuery<T> andGreat(Property<T, ?> property, Object value) {
        super.appendAndSql(getColumnName(property), value, ">");
        return this;

    }

    /**
     * 大于等于
     */
    public LambdaQuery<T> andGreatEq(Property<T, ?> property, Object value) {
        appendAndSql(getColumnName(property), value, ">=");
        return this;
    }

    /**
     * 小于
     */
    public LambdaQuery<T> andLess(Property<T, ?> property, Object value) {
        appendAndSql(getColumnName(property), value, "<");
        return this;
    }

    /**
     * 小于等于
     */
    public LambdaQuery<T> andLessEq(Property<T, ?> property, Object value) {
        appendAndSql(getColumnName(property), value, "<=");
        return this;
    }

    public LambdaQuery<T> andLike(Property<T, ?> property, Object value) {
        appendAndSql(getColumnName(property), value, "LIKE ");
        return this;
    }

    public LambdaQuery<T> andNotLike(Property<T, ?> property, Object value) {
        appendAndSql(getColumnName(property), value, "NOT LIKE ");
        return this;
    }

    public LambdaQuery<T> andIsNull(Property<T, ?> property) {
        appendAndSql(getColumnName(property), null, "IS NULL ");
        return this;
    }

    public LambdaQuery<T> andIsNotNull(Property<T, ?> property) {
        appendAndSql(getColumnName(property), null, "IS NOT NULL ");
        return this;
    }

    public LambdaQuery<T> andIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getColumnName(property), value, IN, AND);
        return this;
    }

    public LambdaQuery<T> andNotIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getColumnName(property), value, NOT_IN, AND);
        return this;
    }

    /**
     * 具有过滤空值功能的IN语句判断
     *
     * @param property
     * @param value
     * @return
     */
    public LambdaQuery<T> andIn(Property<T, ?> property, StrongValue value) {
        appendInSql(getColumnName(property), value, IN, AND);
        return this;
    }

    /**
     * 具有过滤空值功能的NOT IN语句判断
     *
     * @param property
     * @param value
     * @return
     */
    public LambdaQuery<T> andNotIn(Property<T, ?> property, StrongValue value) {
        appendInSql(getColumnName(property), value, NOT_IN, AND);
        return this;
    }

    public LambdaQuery<T> andBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getColumnName(property), BETWEEN, AND, value1, value2);
        return this;
    }

    public LambdaQuery<T> andNotBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getColumnName(property), NOT_BETWEEN, AND, value1, value2);
        return this;
    }

    public LambdaQuery<T> orEq(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "=");
        return this;
    }

    public LambdaQuery<T> orNotEq(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "<>");
        return this;
    }

    /**
     * 大于
     */
    public LambdaQuery<T> orGreat(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, ">");
        return this;
    }

    /**
     * 大于等于
     */
    public LambdaQuery<T> orGreatEq(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, ">=");
        return this;
    }

    /**
     * 小于
     */
    public LambdaQuery<T> orLess(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "<");
        return this;
    }

    /**
     * 小于等于
     */
    public LambdaQuery<T> orLessEq(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "<=");
        return this;
    }

    public LambdaQuery<T> orLike(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "LIKE ");
        return this;
    }

    public LambdaQuery<T> orNotLike(Property<T, ?> property, Object value) {
        appendOrSql(getColumnName(property), value, "NOT LIKE ");
        return this;
    }

    public LambdaQuery<T> orIsNull(Property<T, ?> property) {
        appendOrSql(getColumnName(property), null, "IS NULL ");
        return this;
    }

    public LambdaQuery<T> orIsNotNull(Property<T, ?> property) {
        appendOrSql(getColumnName(property), null, "IS NOT NULL ");
        return this;
    }

    public LambdaQuery<T> orIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getColumnName(property), value, IN, OR);
        return this;
    }

    public LambdaQuery<T> orIn(Property<T, ?> property, StrongValue value) {
        appendInSql(getColumnName(property), value, IN, OR);
        return this;
    }

    public LambdaQuery<T> orIn(Property<T, ?> property, Optional value) {
        appendInSql(getColumnName(property), value, IN, OR);
        return this;
    }

    public LambdaQuery<T> orNotIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getColumnName(property), value, NOT_IN, OR);
        return this;
    }

    public LambdaQuery<T> orNotIn(Property<T, ?> property, StrongValue value) {
        appendInSql(getColumnName(property), value, NOT_IN, OR);
        return this;
    }

    public LambdaQuery<T> orNotIn(Property<T, ?> property, Optional value) {
        appendInSql(getColumnName(property), value, NOT_IN, OR);
        return this;
    }

    public LambdaQuery<T> orBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getColumnName(property), BETWEEN, OR, value1, value2);
        return this;
    }

    public LambdaQuery<T> orNotBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getColumnName(property), NOT_BETWEEN, OR, value1, value2);
        return this;
    }

    public LambdaQuery<T> groupBy(Property<T, ?> property) {
        super.groupBy(getColumnName(property));

        return this;
    }

    public LambdaQuery<T> orderBy(Property<T, ?> property) {
        super.orderBy(getColumnName(property));
        return this;
    }

    public LambdaQuery<T> asc(Property<T, ?> property) {
        super.asc(getColumnName(property));

        return this;
    }

    public LambdaQuery<T> desc(Property<T, ?> property) {
        super.desc(getColumnName(property));
        return this;
    }

	public String getColumnName(Property<T, ?> property) {
		String attr = getAttrName(property);
		return sqlManager.getNc().getColName(clazz, attr);

    }

	protected String getAttrName(Property<T, ?> property){
		try {
			Method declaredMethod = property.getClass().getDeclaredMethod("writeReplace");
			declaredMethod.setAccessible(Boolean.TRUE);
			SerializedLambda serializedLambda = (SerializedLambda) declaredMethod.invoke(property);
			String method = serializedLambda.getImplMethodName();
			String attr = null;
			if (method.startsWith("get")) {
				attr = method.substring(3);
			} else {
				attr = method.substring(2);
			}
			attr = StringKit.toLowerCaseFirstOne(attr);
			return attr;
		}catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public Class getColumnType(Property<T, ?> property) {
		try {
			Method declaredMethod = property.getClass().getDeclaredMethod("writeReplace");
			declaredMethod.setAccessible(Boolean.TRUE);
			SerializedLambda serializedLambda = (SerializedLambda) declaredMethod.invoke(property);
			String method = serializedLambda.getImplMethodName();
			Class type = clazz.getMethod(method,new Class[0]).getReturnType();
			return type;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

	}

	public String[] getColumnNames(Property<T, ?>... properties) {
        String[] cols = new String[properties.length];
        int i = 0;
        for (Property<T, ?> property : properties) {
            cols[i++] = this.getColumnName(property);
        }
        return cols;

    }

    @Override
    public LambdaQuery<T> condition() {
        return new LambdaQuery(this.sqlManager, clazz);
    }


    //下面是重写父类一些方法，将返回值修改为LambdaQuery，这样方便继续链式lambda调用

    @Override
    public LambdaQuery<T> and(QueryCondition condition) {
        super.and(condition);
        return this;
    }


    public Query<T> and(Consumer<QueryCondition> consumer) {
        QueryCondition condition = condition();
        consumer.accept(condition);
        return and(condition);
    }

	@Override
	public LambdaQuery<T> asTable(String tableName){
    	super.asTable(tableName);
    	return this;
	}

	@Override
	public LambdaQuery<T> virtualTable() {
		super.virtualTable();
		return this;
	}

    @Override
    public LambdaQuery<T> or(QueryCondition condition) {
        super.or(condition);
        return this;
    }

    public Query<T> or(Consumer<QueryCondition> consumer) {
        QueryCondition condition = condition();
        consumer.accept(condition);
        return or(condition);
    }

    @Override
    public LambdaQuery<T> having(QueryCondition condition) {
        super.having(condition);
        return this;
    }

    public Query<T> having(Consumer<QueryCondition> consumer) {
        QueryCondition condition = condition();
        consumer.accept(condition);
        return having(condition);
    }

    @Override
    public LambdaQuery<T> limit(Object startRow, long pageSize) {
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
    public void addPreParam(List<Object> objects) {
        super.addPreParam(objects);
    }

    @Override
    public LambdaQuery<T> addParam(Object object) {
        super.addParam(object);
        return this;
    }

    @Override
    public LambdaQuery<T> groupBy(String column) {
        super.groupBy(column);
        return this;
    }

    //下面所有的方法是将父类的方法重写一次并标注为废弃，这样防止在链式调用中调用到父类方法导致无法继续使用lambda

    @Override
    public LambdaQuery<T> orderBy(String orderBy) {
        super.orderBy(orderBy);
        return this;
    }

    @Override
    public LambdaQuery<T> asc(String column) {
        super.asc(column);
        return this;
    }

    @Override
    public LambdaQuery<T> desc(String column) {
        super.desc(column);
        return this;
    }

    @Override
    public LambdaQuery<T> andEq(String column, Object value) {
        super.andEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotEq(String column, Object value) {
        super.andNotEq(column, value);
        return this;
    }

    /**
     * 大于
     */
    @Override
    public LambdaQuery<T> andGreat(String column, Object value) {
        super.andGreat(column, value);
        return this;
    }

    /**
     * 大于等于
     */
    @Override
    public LambdaQuery<T> andGreatEq(String column, Object value) {
        super.andGreatEq(column, value);
        return this;
    }

    /**
     * 小于
     */
    @Override
    public LambdaQuery<T> andLess(String column, Object value) {
        super.andLess(column, value);
        return this;
    }

    /**
     * 小于等于
     */
    @Override
    public LambdaQuery<T> andLessEq(String column, Object value) {
        super.andLessEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andLike(String column, Object value) {
        super.andLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotLike(String column, Object value) {
        super.andNotLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andIsNull(String column) {
        super.andIsNull(column);
        return this;
    }

    @Override
    public LambdaQuery<T> andIsNotNull(String column) {
        super.andIsNotNull(column);
        return this;
    }

    @Override
    public LambdaQuery<T> andIn(String column, Collection<?> value) {
        super.andIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andIn(String column, StrongValue value) {
        super.andIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotIn(String column, Collection<?> value) {
        super.andNotIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotIn(String column, StrongValue value) {
        super.andNotIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andBetween(String column, Object value1, Object value2) {
        super.andBetween(column, value1, value2);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotBetween(String column, Object value1, Object value2) {
        super.andNotBetween(column, value1, value2);
        return this;
    }

    @Override
    public LambdaQuery<T> orEq(String column, Object value) {
        super.orEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orNotEq(String column, Object value) {
        super.orNotEq(column, value);
        return this;
    }

    /**
     * 大于
     */
    @Override
    public LambdaQuery<T> orGreat(String column, Object value) {
        super.orGreat(column, value);
        return this;
    }

    /**
     * 大于等于
     */
    @Override
    public LambdaQuery<T> orGreatEq(String column, Object value) {
        super.orGreatEq(column, value);
        return this;
    }

    /**
     * 小于
     */
    @Override
    public LambdaQuery<T> orLess(String column, Object value) {
        super.orLess(column, value);
        return this;
    }

    /**
     * 小于等于
     */
    @Override
    public LambdaQuery<T> orLessEq(String column, Object value) {
        super.orLessEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orLike(String column, Object value) {
        super.orLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orNotLike(String column, Object value) {
        super.orNotLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orIsNull(String column) {
        super.orIsNull(column);
        return this;
    }

    @Override
    public LambdaQuery<T> orIsNotNull(String column) {
        super.orIsNotNull(column);
        return this;
    }

    @Override
    public LambdaQuery<T> orIn(String column, Collection<?> value) {
        super.orIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orIn(String column, StrongValue value) {
        super.orIn(column, value);
        return this;
    }


    @Override
    public LambdaQuery<T> orNotIn(String column, Collection<?> value) {
        super.orNotIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orNotIn(String column, StrongValue value) {
        super.orNotIn(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orBetween(String column, Object value1, Object value2) {
        super.orBetween(column, value1, value2);
        return this;
    }

    @Override
    public LambdaQuery<T> orNotBetween(String column, Object value1, Object value2) {
        super.orNotBetween(column, value1, value2);
        return this;
    }

    public interface Property<T, R> extends Function<T, R>, Serializable {
    }

    @Override
    public LambdaQuery<T> useCondition(QueryCondition condition) {
        super.useCondition(condition);
        return this;
    }


	/**
	 * 更新一个属性和值
	 * @param property
	 * @param value
	 * @return
	 */
	public int updateParams(Property<T, ?> property, Object value){
		return updateParams(property,value,null,null);
	}

	/**
	 * 更新俩个属性和值对
	 * @param property
	 * @param value
	 * @param property2
	 * @param value2
	 * @return
	 */
	public int updateParams(Property<T, ?> property, Object value,Property<T, ?> property2, Object value2){
		return updateParams(property,value,property2,value2,null,null);
	}

	/**
	 * 更新3个属性和值对
	 * @param property
	 * @param value
	 * @param property2
	 * @param value2
	 * @param property3
	 * @param value3
	 * @return
	 */
	public int updateParams(Property<T, ?> property, Object value,Property<T, ?> property2, Object value2,Property<T, ?> property3, Object value3){
		Map<String, Object> params = new HashMap<>();
		params.put(getAttrName(property),value);
		if(property2!=null){
			params.put(getAttrName(property2),value2);
		}
		if(property3!=null){
			params.put(getAttrName(property3),value3);
		}
		return updateParams(params);
	}


}
