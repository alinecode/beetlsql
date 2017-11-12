package org.beetl.sql.core.query;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SQLScript;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;
import org.beetl.sql.core.query.interfacer.QueryOtherI;

import java.util.List;

/**
 * @author GavinKing
 * @ClassName: Query
 * @Description:查询器
 * @date 2017/11/5
 */
public class Query<T> extends QueryCondition implements QueryExecuteI<T>,QueryOtherI<Query> {

    Class<T> clazz = null;

    public Query(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public List<T> select(String... columns) {
        StringBuilder sb = new StringBuilder("SELECT ");
        for (String column : columns) {
            sb.append(column).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" FROM ").append(QueryTool.getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        List list = QueryTool.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()),
                clazz
        );
        return list;
    }

    @Override
    public List<T> select() {
        StringBuilder sb = new StringBuilder("SELECT * ");
        sb.append("FROM ").append(QueryTool.getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        List list = QueryTool.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()),
                clazz
        );
        return list;
    }

    @Override
    public int update(T t) {
        SQLSource sqlSource = QueryTool.sqlManager.getDbStyle().genUpdateAll(t.getClass());
        GroupTemplate gt = QueryTool.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getId());
        SQLScript sqlScript = new SQLScript(sqlSource, QueryTool.sqlManager);
        sqlScript.update(t);
        template.binding("_params", t);
        String sql = template.render();
        StringBuilder sb = new StringBuilder(sql);
        sb.append(" ").append(getSql());
        this.setSql(sb);
        int row = QueryTool.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int updateSelective(T t) {
        SQLSource sqlSource = QueryTool.sqlManager.getDbStyle().genUpdateTemplate(t.getClass());
        GroupTemplate gt = QueryTool.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getId());
        SQLScript sqlScript = new SQLScript(sqlSource, QueryTool.sqlManager);
        sqlScript.update(t);
        template.binding("_params", t);
        String sql = template.render();
        StringBuilder sb = new StringBuilder(sql);
        sb.append(" ").append(getSql());
        this.setSql(sb);
        int row = QueryTool.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int insert(T t) {
        SQLSource sqlSource = QueryTool.sqlManager.getDbStyle().genInsert(t.getClass());
        GroupTemplate gt = QueryTool.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getId());
        SQLScript sqlScript = new SQLScript(sqlSource, QueryTool.sqlManager);
        sqlScript.update(t);
        template.binding("_params", t);
        String sql = template.render();
        StringBuilder sb = new StringBuilder(sql);
        sb.append(" ").append(getSql());
        this.setSql(sb);
        int row = QueryTool.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int insertSelective(T t) {
        SQLSource sqlSource = QueryTool.sqlManager.getDbStyle().genInsertTemplate(t.getClass());
        GroupTemplate gt = QueryTool.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getId());
        SQLScript sqlScript = new SQLScript(sqlSource, QueryTool.sqlManager);
        sqlScript.update(t);
        template.binding("_params", t);
        String sql = template.render();
        StringBuilder sb = new StringBuilder(sql);
        sb.append(" ").append(getSql());
        this.setSql(sb);
        int row = QueryTool.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int delete() {
        StringBuilder sb = new StringBuilder("DELETE ");
        sb.append("FROM ").append(QueryTool.getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        int row = QueryTool.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int count() {
        return 0;
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
    public Query limit(Long startRow, Long rowCount) {
        //TODO 兼容其他数据库
        this.appendSql("LIMIT ?,?");
        this.addParam(startRow);
        this.addParam(rowCount);
        return this;
    }

}
