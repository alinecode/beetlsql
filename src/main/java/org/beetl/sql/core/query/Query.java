package org.beetl.sql.core.query;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;
import org.beetl.sql.core.query.interfacer.QueryOtherI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author GavinKing

 */
public class Query<T> extends QueryCondition<T> implements QueryExecuteI<T>, QueryOtherI<Query> {

    Class<T> clazz = null;

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
    
    public LamdbaQuery<T> lambda() {
    	if (BeanKit.queryLambdasSupport) {
    		return new LamdbaQuery(this.sqlManager, clazz);
		} else {
			throw new UnsupportedOperationException("需要使用Java8以上，并且依赖com.trigersoft:jaque,请查阅官网文档");
		}
    	
    }


    @Override
    public List<T> select(String... columns) {
        StringBuilder sb = new StringBuilder("SELECT ");
        for (String column : columns) {
            sb.append(column).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" FROM ").append(getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        List<T> list = this.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()),
                clazz
        );
        return list;
    }

    @Override
    public  T single() {
        List<T> list = limit(getFirtRowNumber(),1).select();
        if(list.isEmpty()){
            return null;
        }
        //同SQLManager.single 一致，只取第一条。
        return list.get(0);
    }
    
    @Override
    public T unique() {
        List<T> list = limit(getFirtRowNumber(),2).select();
        if(list.isEmpty()){
              throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，但数据库未找到结果集");
        }else if(list.size()!=1) {
        	throw new BeetlSQLException(BeetlSQLException.UNIQUE_EXCEPT_ERROR, "unique查询，查询出多条结果集");
        }
        return list.get(0);
    }
    
    private int getFirtRowNumber() {
    	return this.sqlManager.isOffsetStartZero()?0:1;
    }

    @Override
    public List<T> select() {
        StringBuilder sb = new StringBuilder("SELECT * ");
        sb.append("FROM ").append(getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        List<T> list = this.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()),
                clazz
        );
        return list;
    }

    @Override
    public int update(T t) {
        SQLSource sqlSource = this.sqlManager.getDbStyle().genUpdateAbsolute(t.getClass());
        return handlerUpdateSql(t, sqlSource);
    }

    @Override
    public int updateSelective(T t) {
        SQLSource sqlSource = this.sqlManager.getDbStyle().genUpdateAll(t.getClass());
        return handlerUpdateSql(t, sqlSource);
    }

    private int handlerUpdateSql(T t, SQLSource sqlSource) {
        GroupTemplate gt = this.sqlManager.getBeetl().getGroupTemplate();

        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg = gt.getConf();
        GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, cfg);
        Template template = groupTemplate.getTemplate(sqlSource.getTemplate());
        template.binding("_paras", new ArrayList<Object>());
        template.binding(BeanKit.objectToMap(t));
        String sql = template.render();
        int i = sql.lastIndexOf(",\r\n");
        if (i == sql.length() - 3) {
            sql = sql.substring(0, i);
        }
        List<SQLParameter> param = (List<SQLParameter>) template.getCtx().getGlobal("_paras");
        List<Object> paraLis = new ArrayList<Object>();
        for (SQLParameter sqlParameter : param) {
            paraLis.add(sqlParameter.value);
        }

        addPreParam(paraLis);

        StringBuilder sb = new StringBuilder(sql);

        sb.append(" ").append(getSql());

        this.setSql(sb);

        int row = this.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }

    @Override
    public int insert(T t) {
        return this.sqlManager.insert(t, true);
    }

    @Override
    public int insertSelective(T t) {
        return this.sqlManager.insertTemplate(t, true);
    }

    @Override
    public int delete() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        int row = this.sqlManager.executeUpdate(
                new SQLReady(getSql().toString(), getParams().toArray())
        );
        return row;
    }


	@Override
    public long count() {
        StringBuilder sb = new StringBuilder("SELECT COUNT(1) FROM ");
        sb.append(getTableName(clazz))
                .append(" ").append(getSql());
        this.setSql(sb);
        List results = this.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()), Long.class
        );
        return (Long) results.get(0);
    }

    @Override
    public Query<T> having(QueryCondition condition) {
        //去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }
        this.appendSql("HAVING ")
                .appendSql(condition.getSql().toString())
                .appendSql(" ");
        this.addParam(condition.getParams());
        return this;
    }

    @Override
    public Query<T> groupBy(String column) {
        this.appendSql("GROUP BY ")
                .appendSql(column)
                .appendSql(" ");
        return this;
    }

    @Override
    public Query<T> orderBy(String orderBy) {
        this.appendSql("ORDER BY ")
                .appendSql(orderBy)
                .appendSql(" ");
        return this;
    }

    /**
     * 默认从1开始，自动翻译成数据库的起始位置。如果配置了OFFSET_START_ZERO =true，则从0开始。
     */
    @Override
    public Query<T> limit(long startRow, long pageSize) {
        setSql(new StringBuilder(sqlManager.getDbStyle().getPageSQLStatement(this.getSql().toString(), startRow, pageSize)));
        return this;
    }

}
