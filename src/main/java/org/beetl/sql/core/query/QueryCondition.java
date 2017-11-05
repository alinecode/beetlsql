package org.beetl.sql.core.query;

import java.util.Collection;

public class QueryCondition implements QueryConditionInterFace<QueryCondition>{

    private StringBuilder sql = null;

    /**
     * 拼接SQL
     * @param sqlPart
     */
    public void appendSql(String sqlPart){
        if(this.sql == null){
            this.sql = new StringBuilder();
        }
        sql.append(sqlPart);
    }
    /**
     * 获取一个新条件
     * @return
     */
    public static QueryCondition condition(){
        return new QueryCondition();
    }

    @Override
    public QueryCondition andEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andNotEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andGt(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andGEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andLt(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andLEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition andLike(String column, String value) {
        return null;
    }

    @Override
    public QueryCondition andNotLike(String column, String value) {
        return null;
    }

    @Override
    public QueryCondition andIsNull(String column) {
        return null;
    }

    @Override
    public QueryCondition andIsNotNull(String column) {
        return null;
    }

    @Override
    public QueryCondition andExists(String column) {
        return null;
    }

    @Override
    public QueryCondition andNotExists(String column) {
        return null;
    }

    @Override
    public QueryCondition andIn(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition andNotIn(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition andBetween(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition andNotBetween(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition orEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orNotEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orGt(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orGEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orLt(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orLEq(String column, Object value) {
        return null;
    }

    @Override
    public QueryCondition orLike(String column, String value) {
        return null;
    }

    @Override
    public QueryCondition orNotLike(String column, String value) {
        return null;
    }

    @Override
    public QueryCondition orIsNull(String column) {
        return null;
    }

    @Override
    public QueryCondition orIsNotNull(String column) {
        return null;
    }

    @Override
    public QueryCondition orExists(String column) {
        return null;
    }

    @Override
    public QueryCondition orNotExists(String column) {
        return null;
    }

    @Override
    public QueryCondition orIn(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition orNotIn(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition orBetween(String column, Collection<?> value) {
        return null;
    }

    @Override
    public QueryCondition orNotBetween(String column, Collection<?> value) {
        return null;
    }

    @Override
    public String getSql() {
        return null;
    }
}
