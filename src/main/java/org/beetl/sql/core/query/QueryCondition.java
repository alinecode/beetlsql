package org.beetl.sql.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryCondition implements QueryConditionInterFace<QueryCondition> {

    private StringBuilder sql = null;
    private List<Object> params = new ArrayList<Object>();
    private final String AND = "AND";
    private final String OR = "OR";
    private final String WHERE = "WHERE";

    /**
     * 拼接SQL
     *
     * @param sqlPart
     */
    public QueryCondition appendSql(String sqlPart) {
        if (this.sql == null) {
            this.sql = new StringBuilder();
        }
        sql.append(sqlPart);
        return this;
    }

    /**
     * 增加参数
     */
    public QueryCondition addParam(Collection<?> objects) {
        params.addAll(objects);
        return this;
    }

    /**
     * 增加参数
     *
     * @param object
     */
    public QueryCondition addParam(Object object) {
        params.add(object);
        return this;
    }


    /**
     * 获取一个新条件
     *
     * @return
     */
    public static QueryCondition condition() {
        return new QueryCondition();
    }

    private void appendAndSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, AND);
    }

    private void appendOrSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, OR);
    }

    private void appendSqlBase(String column, Object value, String opt, String link) {
        if (getSql().indexOf(WHERE) < 0) {
            this.appendSql(WHERE).appendSql(" ");
        }
        if(value != null){
            this.appendSql(link).appendSql(" `").appendSql(column).appendSql("` ").appendSql(opt)
                    .appendSql(" ? ");
            this.addParam(value);
        }else {
            this.appendSql(link).appendSql(" `").appendSql(column).appendSql("` ").appendSql(opt);
        }
    }

    @Override
    public QueryCondition andEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return this;
    }

    @Override
    public QueryCondition andNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return this;
    }

    @Override
    public QueryCondition andGt(String column, Object value) {
        appendAndSql(column, value, ">");
        return this;
    }

    @Override
    public QueryCondition andGEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return this;
    }

    @Override
    public QueryCondition andLt(String column, Object value) {
        appendAndSql(column, value, "<");
        return this;
    }

    @Override
    public QueryCondition andLEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return this;
    }

    @Override
    public QueryCondition andLike(String column, String value) {
        appendAndSql(column, value, "LIKE");
        return this;
    }

    @Override
    public QueryCondition andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE");
        return this;
    }

    @Override
    public QueryCondition andIsNull(String column) {
        appendAndSql(column, null, "IS NULL");
        return this;
    }

    @Override
    public QueryCondition andIsNotNull(String column) {
        return this;
    }

    @Override
    public QueryCondition andExists(String column) {
        return this;
    }

    @Override
    public QueryCondition andNotExists(String column) {
        return this;
    }

    @Override
    public QueryCondition andIn(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition andNotIn(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition andBetween(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition andNotBetween(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition orEq(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orNotEq(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orGt(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orGEq(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orLt(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orLEq(String column, Object value) {
        return this;
    }

    @Override
    public QueryCondition orLike(String column, String value) {
        return this;
    }

    @Override
    public QueryCondition orNotLike(String column, String value) {
        return this;
    }

    @Override
    public QueryCondition orIsNull(String column) {
        return this;
    }

    @Override
    public QueryCondition orIsNotNull(String column) {
        return this;
    }

    @Override
    public QueryCondition orExists(String column) {
        return this;
    }

    @Override
    public QueryCondition orNotExists(String column) {
        return this;
    }

    @Override
    public QueryCondition orIn(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition orNotIn(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition orBetween(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition orNotBetween(String column, Collection<?> value) {
        return this;
    }

    @Override
    public QueryCondition and(QueryCondition condition) {
        return this;
    }

    @Override
    public QueryCondition or(QueryCondition condition) {
        return this;
    }

    @Override
    public StringBuilder getSql() {
        return this.sql;
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

}
