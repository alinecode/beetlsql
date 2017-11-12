package org.beetl.sql.core.query;

import org.beetl.sql.core.query.interfacer.QueryConditionI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryCondition implements QueryConditionI {

    private StringBuilder sql = null;
    private List<Object> params = new ArrayList<Object>();
    private final String AND = "AND";
    private final String OR = "OR";
    private final String WHERE = "WHERE";
    private final String IN = "IN";
    private final String NOT_IN = "NOT IN";
    private final String BETWEEN = "BETWEEN";
    private final String NOT_BETWEEN = "NOT BETWEEN";

    /**
     * 拼接SQL
     *
     * @param sqlPart
     */
    public Query appendSql(String sqlPart) {
        if (this.sql == null) {
            this.sql = new StringBuilder();
        }
        sql.append(sqlPart);
        return (Query) this;
    }

    /**
     * 增加参数
     */
    public Query addParam(Collection<?> objects) {
        params.addAll(objects);
        return (Query) this;
    }

    /**
     * 增加参数
     *
     * @param object
     */
    public Query addParam(Object object) {
        params.add(object);
        return (Query) this;
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
            link = WHERE;
        }
        this.appendSql(link).appendSql(" `").appendSql(column).appendSql("` ").appendSql(opt);
        if (value != null) {
            this.appendSql(" ? ");
            this.addParam(value);
        }
    }

    private void appendInSql(String column, Collection<?> value, String opt, String link) {
        if (getSql().indexOf(link) < 0) {
            link = "";
        }
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }

        this.appendSql(link).appendSql(" `").appendSql(column).appendSql("` ").appendSql(opt)
                .appendSql("(");
        for (Object o : value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        this.getSql().deleteCharAt(this.getSql().length() - 1);
        this.appendSql(")");
    }

    private void appendBetweenSql(String column, String opt, String link, Object... value) {
        if (getSql().indexOf(link) < 0) {
            link = "";
        }
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }

        this.appendSql(link).appendSql(" `").appendSql(column).appendSql("` ").appendSql(opt)
                .appendSql(" ? AND ?");
        this.addParam(value[0]);
        this.addParam(value[1]);
    }

    @Override
    public Query andEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return (Query) this;
    }

    @Override
    public Query andNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return (Query) this;
    }

    @Override
    public Query andGreat(String column, Object value) {
        appendAndSql(column, value, ">");
        return (Query) this;
    }

    @Override
    public Query andGreatEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return (Query) this;
    }

    @Override
    public Query andLess(String column, Object value) {
        appendAndSql(column, value, "<");
        return (Query) this;
    }

    @Override
    public Query andLessEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return (Query) this;
    }

    @Override
    public Query andLike(String column, String value) {
        appendAndSql(column, value, "LIKE");
        return (Query) this;
    }

    @Override
    public Query andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE");
        return (Query) this;
    }

    @Override
    public Query andIsNull(String column) {
        appendAndSql(column, null, "IS NULL");
        return (Query) this;
    }

    @Override
    public Query andIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL");
        return (Query) this;
    }

    @Override
    public Query andIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, AND);
        return (Query) this;
    }

    @Override
    public Query andNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return (Query) this;
    }

    @Override
    public Query andBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, AND, value1, value2);
        return (Query) this;
    }

    @Override
    public Query andNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, AND, value1, value2);
        return (Query) this;
    }

    @Override
    public Query orEq(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orNotEq(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orGreat(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orGreatEq(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orLess(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orLessEq(String column, Object value) {
        return (Query) this;
    }

    @Override
    public Query orLike(String column, String value) {
        return (Query) this;
    }

    @Override
    public Query orNotLike(String column, String value) {
        return (Query) this;
    }

    @Override
    public Query orIsNull(String column) {
        return (Query) this;
    }

    @Override
    public Query orIsNotNull(String column) {
        return (Query) this;
    }

    @Override
    public Query orIn(String column, Collection<?> value) {
        return (Query) this;
    }

    @Override
    public Query orNotIn(String column, Collection<?> value) {
        return (Query) this;
    }

    @Override
    public Query orBetween(String column, Object value1, Object value2) {
        return (Query) this;
    }

    @Override
    public Query orNotBetween(String column, Object value1, Object value2) {
        return (Query) this;
    }

    @Override
    public Query and(QueryCondition condition) {
        return (Query) this;
    }

    @Override
    public Query or(QueryCondition condition) {
        return (Query) this;
    }

    @Override
    public StringBuilder getSql() {
        if (this.sql == null) {
            return new StringBuilder("");
        }
        return this.sql;
    }

    @Override
    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

}
