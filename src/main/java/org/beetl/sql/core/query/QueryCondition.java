package org.beetl.sql.core.query;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.interfacer.QueryConditionI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueryCondition implements QueryConditionI {


    public SQLManager sqlManager;
    private StringBuilder sql = null;
    private List<Object> params = new ArrayList<Object>();
    public final String AND = "AND";
    public final String OR = "OR";
    public final String WHERE = "WHERE";
    private final String IN = "IN";
    private final String NOT_IN = "NOT IN";
    private final String BETWEEN = "BETWEEN";
    private final String NOT_BETWEEN = "NOT BETWEEN";


    private String getCol(String colName) {
        return " " + sqlManager.getDbStyle().getKeyWordHandler().getCol(colName) + " ";
    }

    private String getTable(String tableName) {
        return sqlManager.getDbStyle().getKeyWordHandler().getTable(tableName);
    }

    /****
     * 根据实体class获取表名
     * @param c
     * @return
     */
    public String getTableName(Class<?> c) {
        return getTable(sqlManager.getDbStyle().getNameConversion().getTableName(c));
    }

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
     * 在头部增加参数
     */
    public Query addPreParam(List<Object> objects) {
        objects.addAll(params);
        params = objects;
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
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt);
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

        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql("(");
        for (Object o : value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        this.getSql().deleteCharAt(this.getSql().length() - 1);
        this.appendSql(") ");
    }

    private void appendBetweenSql(String column, String opt, String link, Object... value) {
        if (getSql().indexOf(link) < 0) {
            link = "";
        }
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }

        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ? AND ? ");
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
        appendAndSql(column, value, "LIKE ");
        return (Query) this;
    }

    @Override
    public Query andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE ");
        return (Query) this;
    }

    @Override
    public Query andIsNull(String column) {
        appendAndSql(column, null, "IS NULL ");
        return (Query) this;
    }

    @Override
    public Query andIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL ");
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
        appendOrSql(column, value, "=");
        return (Query) this;
    }

    @Override
    public Query orNotEq(String column, Object value) {
        appendOrSql(column, value, "<>");
        return (Query) this;
    }

    @Override
    public Query orGreat(String column, Object value) {
        appendOrSql(column, value, ">");
        return (Query) this;
    }

    @Override
    public Query orGreatEq(String column, Object value) {
        appendOrSql(column, value, ">=");
        return (Query) this;
    }

    @Override
    public Query orLess(String column, Object value) {
        appendOrSql(column, value, "<");
        return (Query) this;
    }

    @Override
    public Query orLessEq(String column, Object value) {
        appendOrSql(column, value, "<=");
        return (Query) this;
    }

    @Override
    public Query orLike(String column, String value) {
        appendOrSql(column, value, "LIKE");
        return (Query) this;
    }

    @Override
    public Query orNotLike(String column, String value) {
        appendOrSql(column, value, "NOT LIKE");
        return (Query) this;
    }

    @Override
    public Query orIsNull(String column) {
        appendOrSql(column, null, "IS NULL");
        return (Query) this;
    }

    @Override
    public Query orIsNotNull(String column) {
        appendOrSql(column, null, "IS NOT NULL");
        return (Query) this;
    }

    @Override
    public Query orIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, OR);
        return (Query) this;
    }

    @Override
    public Query orNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, OR);
        return (Query) this;
    }

    @Override
    public Query orBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, OR, value1, value2);
        return (Query) this;
    }

    @Override
    public Query orNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, OR, value1, value2);
        return (Query) this;
    }


    @Override
    public Query and(QueryCondition condition) {
        return manyCondition(condition, AND);
    }

    @Override
    public Query or(QueryCondition condition) {
        return manyCondition(condition, OR);
    }

    private Query manyCondition(QueryCondition condition, String link) {
        if (!(condition instanceof QueryCondition)) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_CONDITION_ERROR,
                    "连接条件必须是一个 QueryCondition 类型");
        }

        //去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }

        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        appendSql(link)
                .appendSql(" (")
                .appendSql(condition.getSql().toString())
                .appendSql(")");
        addParam(condition.getParams());
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
