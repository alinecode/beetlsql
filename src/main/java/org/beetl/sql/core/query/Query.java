package org.beetl.sql.core.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;
import org.beetl.sql.core.query.interfacer.QueryOtherI;

/**
 * @author GavinKing
 */
public class Query<T> extends QueryCondition<T> implements QueryExecuteI<T>, QueryOtherI<Query> {

    Class<T> clazz = null;
    StringTemplateResourceLoader tempLoader = new StringTemplateResourceLoader();

    private static final String ALL_COLUMNS = "*";

    public Query(SQLManager sqlManager, Class<T> clazz) {
        this.sqlManager = sqlManager;
        this.clazz = clazz;
    }

    /**
     * 获取一个新条件
     *
     * @return
     */
    public Query<T> condition() {
        return new Query(this.sqlManager, clazz);
    }

    /**
     * 推荐直接使用 dao.createLambdaQuery()/sql.lambdaQuery()来获取
     *
     * @return
     */
    @Deprecated
    public LambdaQuery<T> lambda() {
        if (BeanKit.queryLambdasSupport) {
            if (this.sql != null || this.groupBy != null || this.orderBy != null) {
                throw new UnsupportedOperationException("LamdbaQuery必须在调用其他AP前获取");
            }
            return new LambdaQuery(this.sqlManager, clazz);
        } else {
            throw new UnsupportedOperationException("需要使用Java8以上");
        }

    }

    @Override
    public List<T> select(String... columns) {
        return selectByType(clazz, columns);
    }
    
    @Override
    public List<T> select() {
        return selectByType(clazz, new String[0]);
    }

    /**
     * 拼接字段，不传参数时为*
     *
     * @param columns
     * @return
     */
    private StringBuilder splicingColumns(String[] columns) {
        if (columns == null || columns.length < 1) {
            return new StringBuilder(ALL_COLUMNS);
        }
        StringBuilder columnStr = new StringBuilder();
        for (String column : columns) {
            columnStr.append(column).append(",");
        }
        columnStr.deleteCharAt(columnStr.length() - 1);
        return columnStr;
    }

    @Override
    public T single(String... columns) {
        List<T> list = limit(getFirstRowNumber(), 1).select(columns);
        if (list.isEmpty()) {
            return null;
        }
        // 同SQLManager.single 一致，只取第一条。
        return list.get(0);
    }

    @Override
    public Map mapSingle(String... columns) {
        List<Map> list = limit(getFirstRowNumber(), 1).selectByType(Map.class, columns);
        if (list.isEmpty()) {
            return null;
        }
        // 同SQLManager.single 一致，只取第一条
        return list.get(0);
    }

    @Override
    public T unique() {
        List<T> list = limit(getFirstRowNumber(), 2).select();
        if (list.isEmpty()) {
            throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集");
        } else if (list.size() != 1) {
            throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，查询出多条结果集");
        }
        return list.get(0);


    }

    private int getFirstRowNumber() {
        return this.sqlManager.isOffsetStartZero() ? 0 : 1;
    }

    @Override
    public <K> List<K> select(Class<K> retType, String... columns) {
        return this.selectByType(retType, columns);
    }

    @Override
    public List<Map> mapSelect(String... columns) {
        return this.selectByType(Map.class, columns);
    }

    protected <K> List<K> selectByType(Class<K> retType, String... columns) {
        String column = splicingColumns(columns).toString();
        assembleSelectSql(column);
        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        List<K> list = this.sqlManager.execute(new SQLReady(targetSql, paras), retType);
        return list;
    }

    /***
     * 组装查询的sql语句
     * @return
     */
    private void assembleSelectSql(String column) {
        StringBuilder sb = new StringBuilder("SELECT ").append(column).append(" ");
        sb.append("FROM ").append(getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);
        addAdditionalPartSql();
    }

    /**
     * 增加分页，分组排序
     */
    private void addAdditionalPartSql() {
        addGroupAndOrderPartSql();
        // 增加翻页
        if (this.startRow != -1) {
            setSql(new StringBuilder(
                    sqlManager.getDbStyle().getPageSQLStatement(this.getSql().toString(), startRow, pageSize)));
        }
    }

    /**
     * 增加分组，排序
     */
    private void addGroupAndOrderPartSql() {
        StringBuilder sb = this.getSql();
        if (this.orderBy != null) {
            sb.append(orderBy.getOrderBy()).append(" ");
        }

        if (this.groupBy != null) {
            sb.append(groupBy.getGroupBy()).append(" ");
        }
    }

    @Override
    public int update(Object t) {
        SQLSource sqlSource = this.sqlManager.getDbStyle().genUpdateAbsolute(clazz);
        return handlerUpdateSql(t, sqlSource);
    }

    @Override
    public int updateSelective(Object t) {
        SQLSource sqlSource = this.sqlManager.getDbStyle().genUpdateAll(clazz);
        return handlerUpdateSql(t, sqlSource);
    }

    private int handlerUpdateSql(Object t, SQLSource sqlSource) {
        if (this.sql == null || this.sql.length() == 0) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_CONDITION_ERROR, "update操作没有输入过滤条件会导致更新所有记录");
        }

        GroupTemplate gt = this.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getTemplate(), this.tempLoader);
        template.binding("_paras", new ArrayList<Object>());
        template.binding("_root", t);
        String sql = template.render();
        List<SQLParameter> param = (List<SQLParameter>) template.getCtx().getGlobal("_paras");
        List<Object> paraLis = new ArrayList<Object>();
        for (SQLParameter sqlParameter : param) {
            paraLis.add(sqlParameter.value);
        }

        addPreParam(paraLis);

        StringBuilder sb = new StringBuilder(sql);

        sb.append(" ").append(getSql());

        this.setSql(sb);

        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        int row = this.sqlManager.executeUpdate(new SQLReady(targetSql, paras));
        return row;
    }

    @Override
    public int insert(T t) {
        int ret = this.sqlManager.insert(t, true);
        return ret;
    }

    @Override
    public int insertSelective(T t) {
        return this.sqlManager.insertTemplate(t, true);
    }

    @Override
    public int delete() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);

        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        int row = this.sqlManager.executeUpdate(new SQLReady(targetSql, paras));
        return row;
    }

    @Override
    public long count() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(1) FROM ");
        sb.append(getTableName(clazz)).append(" ").append(getSql());
        this.setSql(sb);

        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        //先清除，避免执行出错后无法清除
        clear();
        List results = this.sqlManager.execute(new SQLReady(targetSql, paras), Long.class);
        return (Long) results.get(0);
    }

    @Override
    public Query<T> having(QueryCondition condition) {
        // 去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }
        if (this.groupBy == null) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_SQL_ERROR, getSqlErrorTip("haveing 需要在groupBy后调用"));
        }

        groupBy.addHaving(condition.getSql().toString());
        this.addParam(condition.getParams());
        return this;
    }

    @Override
    public Query<T> groupBy(String column) {
        GroupBy groupBy = getGroupBy();
        groupBy.add(getCol(column));
        return this;
    }

    @Override
    public Query<T> orderBy(String orderBy) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(orderBy);
        return this;
    }

    @Override
    public Query<T> asc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(getCol(column) + " ASC");
        return this;
    }

    @Override
    public Query<T> desc(String column) {
        OrderBy orderByInfo = this.getOrderBy();
        orderByInfo.add(getCol(column) + " DESC");
        return this;
    }

    private OrderBy getOrderBy() {
        if (this.orderBy == null) {
            orderBy = new OrderBy();
        }
        return this.orderBy;
    }

    private GroupBy getGroupBy() {
        if (this.groupBy == null) {
            groupBy = new GroupBy();
        }
        return this.groupBy;
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public Query<T> limit(long startRow, long pageSize) {
        this.startRow = startRow;
        this.pageSize = pageSize;
        return this;

    }

    protected <K> PageQuery<K> pageByType(long pageNumber, long pageSize, Class<K> retType, String... columns) {
        StringBuilder columnStr = splicingColumns(columns);
        //此处查询语句不需要设置分页
        this.startRow = -1;
        assembleSelectSql(columnStr.toString());
        String targetSql = this.getSql().toString();
        Object[] paras = getParams().toArray();
        SQLReady sqlReady = new SQLReady(targetSql, paras);
        PageQuery<K> pageQuery = new PageQuery<K>(pageNumber, pageSize);
        return this.sqlManager.execute(sqlReady, retType, pageQuery);
    }


    @Override
    public PageQuery<T> page(long pageNumber, long pageSize, String... columns) {
        return pageByType(pageNumber, pageSize, clazz, columns);
    }

    @Override
    public <K> PageQuery<K> page(long pageNumber, long pageSize, Class<K> retType, String... columns) {
        return pageByType(pageNumber, pageSize, retType, columns);
    }

    @Override
    public PageQuery<Map> mapPage(long pageNumber, long pageSize, String... columns) {
        return pageByType(pageNumber, pageSize, Map.class, columns);
    }


    /***
     * 获取错误提示
     *
     * @return
     */
    private String getSqlErrorTip(String couse) {
        return String.format("\n┏━━━━━ SQL语法错误:\n" + "┣SQL：%s\n" + "┣原因：%s\n" + "┣解决办法：您可能需要重新获取一个Query\n" + "┗━━━━━\n",
                getSql().toString(), couse);
    }

}