package org.beetl.sql.core.query;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.kit.StringKit;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @author xiandafu
 */
public class LambdaQuery<T> extends Query<T> {

    public LambdaQuery(SQLManager sqlManager, Class clazz) {
        super(sqlManager, clazz);
    }

    public List<T> select() {
        return super.select();
    }

    public List<T> select(Property<T, ?>... cols) {
        String[] colArray = this.getFunctionName(cols);
        return super.select(colArray);

    }

    public PageQuery<T> page(long pageNumber, long pageSize) {
        return super.page(pageNumber, pageSize);
    }

    public PageQuery<T> page(long pageNumber, long pageSize, Property<T, ?>... cols) {
        String[] colArray = this.getFunctionName(cols);
        return super.page(pageNumber, pageSize, colArray);
    }


    public LambdaQuery<T> andEq(Property<T, ?> property, Object value) {
        super.andEq(getFunctionName(property), value);
        return this;
    }

    public LambdaQuery<T> andNotEq(Property<T, ?> property, Object value) {
        super.andNotEq(getFunctionName(property), value);
        return this;

    }

    public LambdaQuery<T> andGreat(Property<T, ?> property, Object value) {
        super.appendAndSql(getFunctionName(property), value, ">");
        return this;

    }

    public LambdaQuery<T> andGreatEq(Property<T, ?> property, Object value) {
        appendAndSql(getFunctionName(property), value, ">=");
        return this;
    }

    public LambdaQuery<T> andLess(Property<T, ?> property, Object value) {
        appendAndSql(getFunctionName(property), value, "<");
        return this;
    }

    public LambdaQuery<T> andLessEq(Property<T, ?> property, Object value) {
        appendAndSql(getFunctionName(property), value, "<=");
        return this;
    }

    public LambdaQuery<T> andLike(Property<T, ?> property, String value) {
        appendAndSql(getFunctionName(property), value, "LIKE ");
        return this;
    }

    public LambdaQuery<T> andNotLike(Property<T, ?> property, String value) {
        appendAndSql(getFunctionName(property), value, "NOT LIKE ");
        return this;
    }

    public LambdaQuery<T> andIsNull(Property<T, ?> property) {
        appendAndSql(getFunctionName(property), null, "IS NULL ");
        return this;
    }

    public LambdaQuery<T> andIsNotNull(Property<T, ?> property) {
        appendAndSql(getFunctionName(property), null, "IS NOT NULL ");
        return this;
    }

    public LambdaQuery<T> andIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getFunctionName(property), value, IN, AND);
        return this;
    }

    public LambdaQuery<T> andNotIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getFunctionName(property), value, NOT_IN, AND);
        return this;
    }

    public LambdaQuery<T> andBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getFunctionName(property), BETWEEN, AND, value1, value2);
        return this;
    }

    public LambdaQuery<T> andNotBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getFunctionName(property), NOT_BETWEEN, AND, value1, value2);
        return this;
    }

    public LambdaQuery<T> orEq(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, "=");
        return this;
    }

    public LambdaQuery<T> orNotEq(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, "<>");
        return this;
    }

    public LambdaQuery<T> orGreat(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, ">");
        return this;
    }

    public LambdaQuery<T> orGreatEq(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, ">=");
        return this;
    }

    public LambdaQuery<T> orLess(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, "<");
        return this;
    }

    public LambdaQuery<T> orLessEq(Property<T, ?> property, Object value) {
        appendOrSql(getFunctionName(property), value, "<=");
        return this;
    }

    public LambdaQuery<T> orLike(Property<T, ?> property, String value) {
        appendOrSql(getFunctionName(property), value, "LIKE");
        return this;
    }

    public LambdaQuery<T> orNotLike(Property<T, ?> property, String value) {
        appendOrSql(getFunctionName(property), value, "NOT LIKE");
        return this;
    }

    public LambdaQuery<T> orIsNull(Property<T, ?> property) {
        appendOrSql(getFunctionName(property), null, "IS NULL");
        return this;
    }

    public LambdaQuery<T> orIsNotNull(Property<T, ?> property) {
        appendOrSql(getFunctionName(property), null, "IS NOT NULL");
        return this;
    }

    public LambdaQuery<T> orIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getFunctionName(property), value, IN, OR);
        return this;
    }

    public LambdaQuery<T> orNotIn(Property<T, ?> property, Collection<?> value) {
        appendInSql(getFunctionName(property), value, NOT_IN, OR);
        return this;
    }

    public LambdaQuery<T> orBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getFunctionName(property), BETWEEN, OR, value1, value2);
        return this;
    }

    public LambdaQuery<T> orNotBetween(Property<T, ?> property, Object value1, Object value2) {
        appendBetweenSql(getFunctionName(property), NOT_BETWEEN, OR, value1, value2);
        return this;
    }

    public LambdaQuery<T> groupBy(Property<T, ?> property) {
        super.groupBy(getFunctionName(property));

        return this;
    }

    public LambdaQuery<T> orderBy(Property<T, ?> property) {
        super.orderBy(getFunctionName(property));
        return this;
    }

    public LambdaQuery<T> asc(Property<T, ?> property) {
        super.asc(getFunctionName(property));

        return this;
    }

    public LambdaQuery<T> desc(Property<T, ?> property) {
        super.desc(getFunctionName(property));
        return this;
    }

    private String getFunctionName(Property<T, ?> property) {
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
            return sqlManager.getNc().getColName(clazz, StringKit.toLowerCaseFirstOne(attr));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

    }

    private String[] getFunctionName(Property<T, ?>... funs) {
        String[] cols = new String[funs.length];
        int i = 0;
        for (Property<T, ?> property : funs) {
            cols[i++] = this.getFunctionName(property);
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
    public LambdaQuery<T> groupBy(String column) {
        super.groupBy(column);
        return this;
    }

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

    @Override
    public LambdaQuery<T> andGreat(String column, Object value) {
        super.andGreat(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andGreatEq(String column, Object value) {
        super.andGreatEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andLess(String column, Object value) {
        super.andLess(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andLessEq(String column, Object value) {
        super.andLessEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andLike(String column, String value) {
        super.andLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> andNotLike(String column, String value) {
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
    public LambdaQuery<T> andNotIn(String column, Collection<?> value) {
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

    @Override
    public LambdaQuery<T> orGreat(String column, Object value) {
        super.orGreat(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orGreatEq(String column, Object value) {
        super.orGreatEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orLess(String column, Object value) {
        super.orLess(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orLessEq(String column, Object value) {
        super.orLessEq(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orLike(String column, String value) {
        super.orLike(column, value);
        return this;
    }

    @Override
    public LambdaQuery<T> orNotLike(String column, String value) {
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
    public LambdaQuery<T> orNotIn(String column, Collection<?> value) {
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
}
