package org.beetl.sql.core.query;

/**
 * @author GavinKing
 * @ClassName: Query
 * @Description:查询器
 * @date 2017/11/5
 */
public class Query extends QueryCondition implements QueryBaseInterFace<Query> {

    @Override
    public Query select(String... columns) {
        return null;
    }

    @Override
    public Query select() {
        return null;
    }

    @Override
    public Query from(String table) {
        return null;
    }

    @Override
    public Query from() {
        return null;
    }

    @Override
    public Query having(Query condition) {
        return null;
    }

    @Override
    public Query and(Query condition) {
        return null;
    }

    @Override
    public Query or(Query condition) {
        return null;
    }

    @Override
    public Query groupBy(String column) {
        return null;
    }

    @Override
    public Query orderBy(String orderBy) {
        return null;
    }

}
