package org.beetl.sql.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.db.TableDesc;
import org.beetl.sql.core.query.interfacer.QueryConditionI;

public class QueryCondition<T> implements QueryConditionI<T> {
    public SQLManager sqlManager;
    protected StringBuilder sql = null;
    protected List<Object> params = new ArrayList<Object>();
    protected long startRow = -1, pageSize = -1;
    protected OrderBy orderBy = null;
    protected GroupBy groupBy = null;

    public final String AND = "AND";
    public final String OR = "OR";
    public final String WHERE = "WHERE";
    public final String IN = "IN";
    public final String NOT_IN = "NOT IN";
    public final String BETWEEN = "BETWEEN";
    public final String NOT_BETWEEN = "NOT BETWEEN";

    protected QueryCondition() {
    }

    protected void clear() {
        sql = null;
        params = new ArrayList<Object>();
        startRow = -1;
        pageSize = -1;
        orderBy = null;
        groupBy = null;
    }

    protected String getCol(String colName) {
        return " " + sqlManager.getDbStyle().getKeyWordHandler().getCol(colName) + " ";
    }


    /****
     * 根据实体class获取表名
     * @param c
     * @return
     */
    public String getTableName(Class<?> c) {
        String tname = sqlManager.getNc().getTableName(c);
        TableDesc desc = sqlManager.getMetaDataManager().getTable(tname);
        String tabeName2 = desc.getName();
        int index = -1;
        AbstractDBStyle style = (AbstractDBStyle) sqlManager.getDbStyle();
        if ((index = tabeName2.indexOf(style.STATEMENT_START)) != -1) {
            //表名字包含了特殊符号，比如Oracle 的@
            tabeName2 = tabeName2.substring(0, index) + "\\" + tabeName2.substring(index);
        }
        if (desc.getSchema() != null) {
            return style.getKeyWordHandler().getTable(desc.getSchema()) + "." + style.getKeyWordHandler().getTable(tabeName2);
        } else {
            return style.getKeyWordHandler().getTable(tabeName2);
        }
    }


    /**
     * 拼接SQL
     *
     * @param sqlPart
     */
    public Query<T> appendSql(String sqlPart) {
        if (this.sql == null) {
            this.sql = new StringBuilder();
        }
        sql.append(sqlPart);
        return (Query) this;
    }


    /**
     * 增加参数
     *
     * @param objects
     * @return
     */
    public Query<T> addParam(Collection<?> objects) {
        params.addAll(objects);
        return (Query) this;
    }


    /**
     * 在头部增加参数
     *
     * @param objects
     * @return
     */
    public Query<T> addPreParam(List<Object> objects) {
        objects.addAll(params);
        params = objects;
        return (Query) this;
    }

    /**
     * 增加参数
     *
     * @param object
     * @return
     */
    public Query<T> addParam(Object object) {
        params.add(object);
        return (Query) this;
    }

    protected void appendAndSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, AND);
    }

    protected void appendOrSql(String column, Object value, String opt) {
        appendSqlBase(column, value, opt, OR);
    }

    protected void appendSqlBase(String column, Object value, String opt, String link) {
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

    protected void appendInSql(String column, Collection<?> value, String opt, String link) {
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        this.appendSql(link)
                .appendSql(getCol(column))
                .appendSql(opt)
                .appendSql(" ( ");
        for (Object o : value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        this.getSql().deleteCharAt(this.getSql().length() - 1);
        this.appendSql(" ) ");
    }

    protected void appendBetweenSql(String column, String opt, String link, Object... value) {
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
    public Query<T> andEq(String column, Object value) {
        appendAndSql(column, value, "=");
        return (Query) this;
    }

    @Override
    public Query<T> andNotEq(String column, Object value) {
        appendAndSql(column, value, "<>");
        return (Query) this;
    }

    @Override
    public Query<T> andGreat(String column, Object value) {
        appendAndSql(column, value, ">");
        return (Query) this;
    }

    @Override
    public Query<T> andGreatEq(String column, Object value) {
        appendAndSql(column, value, ">=");
        return (Query) this;
    }

    @Override
    public Query<T> andLess(String column, Object value) {
        appendAndSql(column, value, "<");
        return (Query) this;
    }

    @Override
    public Query<T> andLessEq(String column, Object value) {
        appendAndSql(column, value, "<=");
        return (Query) this;
    }

    @Override
    public Query<T> andLike(String column, String value) {
        appendAndSql(column, value, "LIKE ");
        return (Query) this;
    }

    @Override
    public Query<T> andNotLike(String column, String value) {
        appendAndSql(column, value, "NOT LIKE ");
        return (Query) this;
    }

    @Override
    public Query<T> andIsNull(String column) {
        appendAndSql(column, null, "IS NULL ");
        return (Query) this;
    }

    @Override
    public Query<T> andIsNotNull(String column) {
        appendAndSql(column, null, "IS NOT NULL ");
        return (Query) this;
    }

    @Override
    public Query<T> andIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, AND, value1, value2);
        return (Query) this;
    }

    @Override
    public Query<T> andNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, AND, value1, value2);
        return (Query) this;
    }

    @Override
    public Query<T> orEq(String column, Object value) {
        appendOrSql(column, value, "=");
        return (Query) this;
    }

    @Override
    public Query<T> orNotEq(String column, Object value) {
        appendOrSql(column, value, "<>");
        return (Query) this;
    }

    @Override
    public Query<T> orGreat(String column, Object value) {
        appendOrSql(column, value, ">");
        return (Query) this;
    }

    @Override
    public Query<T> orGreatEq(String column, Object value) {
        appendOrSql(column, value, ">=");
        return (Query) this;
    }

    @Override
    public Query<T> orLess(String column, Object value) {
        appendOrSql(column, value, "<");
        return (Query) this;
    }

    @Override
    public Query<T> orLessEq(String column, Object value) {
        appendOrSql(column, value, "<=");
        return (Query) this;
    }

    @Override
    public Query<T> orLike(String column, String value) {
        appendOrSql(column, value, "LIKE");
        return (Query) this;
    }

    @Override
    public Query<T> orNotLike(String column, String value) {
        appendOrSql(column, value, "NOT LIKE");
        return (Query) this;
    }

    @Override
    public Query<T> orIsNull(String column) {
        appendOrSql(column, null, "IS NULL");
        return (Query) this;
    }

    @Override
    public Query<T> orIsNotNull(String column) {
        appendOrSql(column, null, "IS NOT NULL");
        return (Query) this;
    }

    @Override
    public Query<T> orIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, BETWEEN, OR, value1, value2);
        return (Query) this;
    }

    @Override
    public Query<T> orNotBetween(String column, Object value1, Object value2) {
        appendBetweenSql(column, NOT_BETWEEN, OR, value1, value2);
        return (Query) this;
    }


    @Override
    public Query<T> and(QueryCondition condition) {
        return manyCondition(condition, AND);
    }

    @Override
    public Query<T> or(QueryCondition condition) {
        return manyCondition(condition, OR);
    }

    private Query<T> manyCondition(QueryCondition condition, String link) {
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
