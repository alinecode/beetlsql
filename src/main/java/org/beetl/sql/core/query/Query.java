package org.beetl.sql.core.query;

/**
 * @author GavinKing
 * @ClassName: Query
 * @Description:查询器
 * @date 2017/11/5
 */
public class Query<T> extends QueryCondition implements QueryBaseInterFace<Query> {

    private T model;

    @Override
    public Query select(String... columns) {
        this.appendSql("SELECT ");
        for (String column : columns) {
            this.appendSql(column).appendSql(",");
        }
        getSql().deleteCharAt(getSql().length() - 1);
        return this;
    }

    @Override
    public Query select() {
        this.appendSql("SELECT * ");
        return this;
    }

    @Override
    public Query from(String table) {
        this.appendSql("FROM ")
                .appendSql(table)
                .appendSql(" ");
        return this;
    }

    @Override
    public Query from() {
        this.appendSql("FROM ")
                .appendSql(QueryTool.getTableName(model.getClass()))
                .appendSql(" ");
        return this;
    }

    @Override
    public Query having(QueryCondition condition) {
        this.appendSql("HAVING ").appendSql(condition.getSql().toString());
        this.addParam(condition.getParams());
        return this;
    }

    @Override
    public Query groupBy(String column) {
        this.appendSql("GROUP BY ")
                .appendSql(column)
                .appendSql(" ");
        return this;
    }

    @Override
    public Query orderBy(String orderBy) {
        this.appendSql("ORDER BY").appendSql(orderBy);
        return this;
    }

    @Override
    public Query limit(Long startRow,Long rowCount) {
        //TODO 兼容其他数据库
        this.appendSql("LIMIT ?,?");
        this.addParam(startRow);
        this.addParam(rowCount);
        return this;
    }

}
