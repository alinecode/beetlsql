package org.beetl.sql.core.query;

import java.util.ArrayList;
import java.util.List;

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

/**
 * @author GavinKing

 */
public class Query<T> extends QueryCondition<T> implements QueryExecuteI<T>, QueryOtherI<Query> {

    Class<T> clazz = null;
    StringTemplateResourceLoader tempLoader = new StringTemplateResourceLoader();

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
    		LamdbaQuery newQuery =  new LamdbaQuery(this.sqlManager, clazz);
    		if(this.sql!=null||this.groupBy!=null||this.orderBy!=null) {
    			throw new UnsupportedOperationException("LamdbaQuery必须在调用其他AP前获取");
    		}
    		return newQuery;
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
        makeSql();
                
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
        makeSql();
        List<T> list = this.sqlManager.execute(
                new SQLReady(getSql().toString(), getParams().toArray()),
                clazz
        );
        return list;
    }
    /**
     * 增加分页，排序
     */
    private void makeSql() {
    	 StringBuilder sb = this.getSql();
    	 if(this.orderBy!=null) {
         	sb.append(orderBy.getOrderBy()).append(" ");
         }
    	 
    	 if(this.groupBy!=null) {
    		 sb.append(groupBy.getGroupBy()).append(" ");
    	 }
         //增加翻页
         if(this.startRow!=-1) {
         	setSql(new StringBuilder(sqlManager.getDbStyle().getPageSQLStatement(this.getSql().toString(), startRow, pageSize)));
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
    	if(this.sql==null||this.sql.length()==0) {
    		throw new BeetlSQLException(BeetlSQLException.QUERY_CONDITION_ERROR,"update操作没有输入过滤条件会导致更新所有记录");
    	}
    	
        GroupTemplate gt = this.sqlManager.getBeetl().getGroupTemplate();
        Template template = gt.getTemplate(sqlSource.getTemplate(),this.tempLoader);
        template.binding("_paras", new ArrayList<Object>());
        template.binding("_root",t);
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
    	GroupBy groupBy =  getGroupBy();
    	groupBy.add(column);
        return this;
    }

    @Override
    public Query<T> orderBy(String orderBy) {
    	OrderBy  orderByInfo = this.getOrderBy();
    	orderByInfo.add(orderBy);
        return this;
    }
    
    @Override
	public Query<T> asc(String column) {
    	OrderBy  orderByInfo = this.getOrderBy();
    	orderBy.add(column+" ASC");
		return this;
	}

	@Override
	public Query<T> desc(String column) {
		OrderBy  orderByInfo = this.getOrderBy();
    	orderBy.add(column+" DESC");
		return this;
	}
    
	private OrderBy getOrderBy() {
		if(this.orderBy==null) {
			orderBy = new OrderBy();
		}
		return this.orderBy;
	}
	
	private GroupBy getGroupBy() {
		if(this.groupBy==null) {
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
    	this.pageSize =pageSize;
    	return this;
//        setSql(new StringBuilder(sqlManager.getDbStyle().getPageSQLStatement(this.getSql().toString(), startRow, pageSize)));
//        return this;
    }

	

}
