package org.beetl.sql.core.query;

import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.clazz.ClassDesc;
import org.beetl.sql.clazz.ColDesc;
import org.beetl.sql.clazz.NameConversion;
import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.*;
import org.beetl.sql.core.*;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.mapping.StreamData;
import org.beetl.sql.core.page.DefaultPageRequest;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.beetl.sql.core.query.interfacer.QueryExecuteI;
import org.beetl.sql.core.query.interfacer.QueryOtherI;
import org.beetl.sql.core.query.interfacer.StrongValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用于方便的构造简单的sql操作，复杂的sql操作建议直接使用sql语句(非不能，不为也)
 * <pre>@{code
 *     Query<User> query = ... ;
 *     List<User> list = query.andEq("name","lijz").select();
 *
 * }</pre>
 * @author GavinKing
 */
public class Query<T> extends QueryCondition<T> implements QueryExecuteI<T>, QueryOtherI<Query> {

    private static final String ALL_COLUMNS = "*";
    Class<T> clazz;
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

    /**
     * 推荐直接使用 dao.createLambdaQuery()/sql.lambdaQuery()来获取
     *
     * @return
     */
    @Deprecated
    public LambdaQuery<T> lambda() {
        if (BeanKit.queryLambdasSupport) {
            if (this.sql != null || this.groupBy != null || this.orderBy != null) {
                throw new UnsupportedOperationException("LambdaQuery必须在调用其他AP前获取");
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
        return selectByType(clazz);
    }

	@Override
	public StreamData<T> stream() {
		String column = splicingColumns(null);
		if (distinct) {
			column = " DISTINCT " + column;
		}
		StringBuilder sql = assembleSelectSql(column);
		String targetSql = sql.toString();
		Object[] paras = getParams().toArray();
		StreamData<T> streamData = this.sqlManager.streamExecute(new SQLReady(targetSql, paras), clazz);
		this.clear();
		return streamData;
	}

	@Override
    public List<T> selectSimple() {
        return selectByType(clazz, getSimpleColumns());
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
    public T singleSimple() {
        return single(getSimpleColumns());
    }

    @Override
    public T uniqueSimple() {
        return unique(getSimpleColumns());
    }

    @Override
    public T unique(String... cols) {
        List<T> list = limit(getFirstRowNumber(), 2).select(cols);
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
        String column = splicingColumns(columns);
        if (distinct) {
            column = " DISTINCT " + column;
        }
        StringBuilder sql = assembleSelectSql(column);
        String targetSql = sql.toString();
        Object[] paras = getParams().toArray();
        List<K> list = this.sqlManager.execute(new SQLReady(targetSql, paras), retType);
        this.clear();
        return list;
    }



    /***
     * 组装查询的sql语句
     * @return
     */
    private StringBuilder assembleSelectSql(String column) {
        StringBuilder sb = new StringBuilder("SELECT ").append(column);
        StringBuilder  whereSql= getSql();
        sb.append(" FROM ").append(getTableName(clazz)).append(' ').append(whereSql);
        appendLogicDelete(sb, whereSql.length()==0);
        sb = addAdditionalPartSql(sb);
        return sb;
    }

    private void appendLogicDelete(StringBuilder sb,boolean whereAppend){
        if(!sqlManager.isQueryLogicDeleteEnable()) {
            return ;
        }
        NameConversion nameConversion = sqlManager.getNc();
        String tableName = nameConversion.getTableName(clazz);
        TableDesc table = sqlManager.getMetaDataManager().getTable(tableName);
        ClassDesc classDesc = table.genClassDesc(clazz, nameConversion);
        if (classDesc.getClassAnnotation().getLogicDeleteAttrName() == null) {
            return ;
        }
        String col = nameConversion.getColName(clazz, classDesc.getClassAnnotation().getLogicDeleteAttrName());
        Object value = classDesc.getClassAnnotation().getLogicDeleteAttrValue();
        if(whereAppend){
        	sb.append(" WHERE 1=1 ");
		}
        sb.append(" AND ").append(col).append("!=").append(value).append(" ");

    }

    /**
     * 增加分页，分组排序
     */
    private StringBuilder addAdditionalPartSql(StringBuilder sql) {
        addGroupAndOrderPartSql(sql);
        // 增加翻页
        if (this.startRow != null) {
            sql = new StringBuilder(
                    sqlManager.getDbStyle().getRangeSql().toRange(sql.toString(), startRow, pageSize));
        }
        return sql;
    }

    /**
     * 增加分组，排序
     */
    private void addGroupAndOrderPartSql(StringBuilder sql) {
        if (this.orderBy != null && this.groupBy != null) {
            //先group by 后 order by 顺序
            sql.append(groupBy.getGroupBy()).append(' ');
            sql.append(orderBy.getOrderBy()).append(' ');
        } else if (this.orderBy != null) {
            sql.append(orderBy.getOrderBy()).append(' ');
        } else if (this.groupBy != null) {
            sql.append(groupBy.getGroupBy()).append(' ');
        }
    }

    @Override
    public int update(Object t) {
        SqlId id = this.sqlManager.getSqlIdFactory().buildIdentity(clazz, AutoSQLEnum.UPDATE_ALL);
        SQLSource sqlSource = sqlManager.getSqlLoader().queryAutoSQL(id);
        if (sqlSource == null) {
            sqlSource = this.sqlManager.getDbStyle().genUpdateAbsolute(clazz);
            sqlManager.getSqlLoader().addAutoGenSQL(id, sqlSource);
            sqlSource.setId(id);
        }
        return handlerUpdateSql(t, sqlSource);
    }

    @Override
    public int updateSelective(Object t) {
        SqlId id = this.sqlManager.getSqlIdFactory().buildIdentity(clazz, AutoSQLEnum.UPDATE_SELECTIVE);
        SQLSource sqlSource = sqlManager.getSqlLoader().queryAutoSQL(id);
        if (sqlSource == null) {
            sqlSource = this.sqlManager.getDbStyle().genUpdateAll(clazz);
            sqlManager.getSqlLoader().addAutoGenSQL(id, sqlSource);
            sqlSource.setId(id);
        }
        return handlerUpdateSql(t, sqlSource);
    }

    private int handlerUpdateSql(Object t, SQLSource sqlSource) {
        if (this.sql == null || this.sql.length() == 0) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_CONDITION_ERROR, "update操作没有输入过滤条件会导致更新所有记录");
        }

        SQLResult result = this.sqlManager.getSQLResult(sqlSource.getId(), t, true);

        List<Object> paraLis = new ArrayList<>();
        for (SQLParameter sqlParameter : result.jdbcPara) {
            paraLis.add(sqlParameter.value);
        }
        addPreParam(paraLis);

        //条件
        String targetSql = result.jdbcSql + " " + getSql();
        Object[] paras = paraLis.toArray();
        int row = this.sqlManager.executeUpdate(new SQLReady(targetSql, paras));
        this.clear();
        return row;
    }

    @Override
    public int insert(T t) {
        return this.sqlManager.insert(t);
    }

    @Override
    public int insertSelective(T t) {
        return this.sqlManager.insertTemplate(t);
    }

    @Override
    public int delete() {
        String targetSql = "DELETE FROM " + getTableName(clazz) + " " + getSql();
        Object[] paras = getParams().toArray();
        int row = this.sqlManager.executeUpdate(new SQLReady(targetSql, paras));
        this.clear();
        return row;
    }

    @Override
    public long count() {
        String targetSql = "SELECT COUNT(1) FROM " + getTableName(clazz) + " " + getSql();
        Object[] paras = getParams().toArray();
        List results = this.sqlManager.execute(new SQLReady(targetSql, paras), Long.class);
        this.clear();
        return (Long) results.get(0);
    }

    @Override
    public PageResult<T> pageSimple(PageRequest pageRequest) {
        return pageByType(pageRequest, clazz, getSimpleColumns());
    }


    @Override
    public Query<T> having(QueryCondition condition) {
        // 去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }
        if (this.groupBy == null) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_SQL_ERROR, getSqlErrorTip("having 需要在groupBy后调用"));
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
    public Query<T> limit(Object startRow, long pageSize) {
        this.startRow = startRow;
        this.pageSize = pageSize;
        return this;

    }

    protected <K> PageResult<K> pageByType(PageRequest pageRequest, Class<K> retType, String... columns) {
        String columnStr = splicingColumns(columns);
        //此处查询语句不需要设置分页
        this.startRow = null;
        StringBuilder sql = assembleSelectSql(columnStr);
        //检测是否包含groupBy
        if (this.groupBy != null) {
            sql = new StringBuilder("SELECT * FROM (").append(sql).append(") t");
        }
        String targetSql = sql.toString();
        Object[] paras = getParams().toArray();
        SQLReady sqlReady = new SQLReady(targetSql, paras);

        this.clear();
        return this.sqlManager.execute(sqlReady, retType, pageRequest);
    }

    @Override
    public PageResult<T> page(PageRequest pageRequest, String... columns) {
        return pageByType(pageRequest, clazz, columns);
    }

    @Override
    public <K> PageResult<K> page(PageRequest pageRequest, Class<K> retType, String... columns) {
        return pageByType(pageRequest, retType, columns);
    }

    @Override
    public PageResult<Map> mapPage(PageRequest pageRequest, String... columns) {
        return pageByType(pageRequest, Map.class, columns);
    }

    @Override
    public PageResult<T> page(long pageNumber, long pageSize, String... columns) {
        PageRequest pageRequest = DefaultPageRequest.of(pageNumber, (int) pageSize);
        return pageByType(pageRequest, clazz, columns);
    }

    @Override
    public PageResult<T> pageSimple(long pageNumber, long pageSize) {
        return page(pageNumber, pageSize, getSimpleColumns());
    }

    @Override
    public <K> PageResult<K> page(long pageNumber, long pageSize, Class<K> retType, String... columns) {
        PageRequest pageRequest = DefaultPageRequest.of(pageNumber, (int) pageSize);
        return pageByType(pageRequest, retType, columns);
    }

    @Override
    public PageResult<Map> mapPage(long pageNumber, long pageSize, String... columns) {
        PageRequest pageRequest = DefaultPageRequest.of(pageNumber, (int) pageSize);
        return pageByType(pageRequest, Map.class, columns);
    }


    /***
     * 获取错误提示
     *
     * @return
     */
    private String getSqlErrorTip(String cause) {
        return String.format("\n┏━━━━━ SQL语法错误:\n" + "┣SQL：%s\n" + "┣原因：%s\n" + "┣解决办法：您可能需要重新获取一个Query\n" + "┗━━━━━\n",
                getSql().toString(), cause);
    }

    /***
     * 获取错误提示
     *
     * @return
     */
    private String getSqlErrorTip(String cause, String solve) {
        return String.format("\n┏━━━━━ SQL语法错误:\n" + "┣SQL：%s\n" + "┣原因：%s\n" + "┣解决办法：" + solve + "\n" + "┗━━━━━\n",
                getSql().toString(), cause);
    }

    /***
     * 获取简要字段
     * @return
     */
    private String[] getSimpleColumns() {
        String tname = sqlManager.getNc().getTableName(this.clazz);
        TableDesc desc = sqlManager.getMetaDataManager().getTable(tname);
        CaseInsensitiveHashMap<String, ColDesc> colMap = desc.getColsDetail();
        List<String> cols = new ArrayList<>(colMap.size());
        for (Map.Entry<String, Object> entry : colMap.entrySet()) {
            int sqlType = ((ColDesc) entry.getValue()).getSqlType();
            if (!JavaType.isBigType(sqlType)) {
                cols.add(entry.getKey());
            }
        }
        String[] columns = new String[cols.size()];
        cols.toArray(columns);
        return columns;
    }

    /**
     * 拼接字段，不传参数时为*
     *
     * @param columns
     * @return
     */
    private String splicingColumns(String[] columns) {
        if (columns == null || columns.length < 1) {
            return ALL_COLUMNS;
        }
        StringBuilder columnStr = new StringBuilder();
        for (String column : columns) {
            columnStr.append(getColTrunk(column)).append(',');
        }
        columnStr.deleteCharAt(columnStr.length() - 1);
        return columnStr.toString();
    }


    /**
     * 过滤空和NULL的值，
     * 如果为空或者null则不增加查询条件
     *
     * @param value
     * @return
     */
    public static StrongValue filterEmpty(Object value) {
        return new StrongValue() {
            @Override
            public boolean isEffective() {
				return validate(value);
            }

            @Override
            public Object getValue() {
                return value;
            }
        };
    }

    /**
     * 过滤空和NULL的值，
     * 如果为空或者null则不增加查询条件
     *
     * @param value
     * @return
     */
    public static StrongValue filterNull(Object value) {
        return new StrongValue() {
            @Override
            public boolean isEffective() {
                return value != null;
            }

            @Override
            public Object getValue() {
                return value;
            }
        };
    }

	/**
	 * 用于like查询中的非空判断
	 *
	 * @param value 值
	 * @return StrongValue
	 */
	public static StrongValue filterLikeEmpty(Object value) {
		return filterLikeEmpty(value, true, true);
	}

	/**
	 * 用于like查询中的非空判断
	 *
	 * @param value     值
	 * @param leftLike  左边补%
	 * @param rightLike 右补%
	 * @return StrongValue
	 */
	public static StrongValue filterLikeEmpty(Object value, boolean leftLike, boolean rightLike) {
		return new StrongValue() {
			@Override
			public boolean isEffective() {
				return validate(value);
			}

			@Override
			public Object getValue() {
				Object likeExpr = value;
				if (leftLike) {
					likeExpr = "%" + likeExpr;
				}
				if (rightLike) {
					likeExpr = likeExpr + "%";
				}
				return likeExpr;
			}
		};
	}

	/**
	 * 校验参数是否有效
	 */
	private static boolean validate(Object value){
		if (value == null) {
			return false;
		}
		//校验空值
		if (value instanceof String) {
			return !"".equals(value);
		}
		if (value instanceof Collection) {
			return !((Collection) value).isEmpty();
		}
		return true;
	}

	/**
	 * 重用，比如在count前，调用此方法会获取count的参数，此参数可以用于select
	 * @param condition
	 * @return
	 */
	@Override
    public Query<T> useCondition(QueryCondition condition) {
        sql = new StringBuilder(condition.sql);
        params = new ArrayList<>(condition.params);
        startRow = condition.startRow;
        pageSize = condition.pageSize;
		if(condition.orderBy!=null){
			orderBy = new OrderBy(condition.orderBy.sb.toString());
		}
       	if(condition.groupBy!=null){
			groupBy = new GroupBy(condition.groupBy.sb.toString());
		}

        return this;
    }


}
