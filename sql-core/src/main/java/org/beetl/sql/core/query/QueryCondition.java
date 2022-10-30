package org.beetl.sql.core.query;

import org.beetl.sql.clazz.TableDesc;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.KeyWordHandler;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.AbstractDBStyle;
import org.beetl.sql.core.query.interfacer.QueryConditionI;
import org.beetl.sql.core.query.interfacer.StrongValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class QueryCondition<T> implements QueryConditionI<T> {
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String WHERE = "WHERE";
    public static final String IN = "IN";
    public static final String NOT_IN = "NOT IN";
    public static final String BETWEEN = "BETWEEN";
    public static final String NOT_BETWEEN = "NOT BETWEEN";
    public SQLManager sqlManager;
    protected StringBuilder sql = null;
    protected List<Object> params = new ArrayList<>();
    protected Object startRow = null;
    long pageSize = -1;
    protected OrderBy orderBy = null;
    protected GroupBy groupBy = null;
    protected boolean distinct = false;

    protected  String tableName;
    //提醒表名是虚拟表，需要表达式计算真实表名
    boolean asVirtual =false;

    protected QueryCondition() {
    }

    protected void clear() {
        sql = null;
        params = new ArrayList<>();
        startRow = null;
        pageSize = -1;
        orderBy = null;
        groupBy = null;
    }

    /**
     * 获取字段 加上前后空格
     *
     * @param colName
     * @return
     */
    protected String getCol(String colName) {
        return " " + getColTrunk(colName) + " ";
    }

    /***
     * 获取字段信息
     * @param colName
     * @return
     */
    protected String getColTrunk(String colName) {
        KeyWordHandler keyWordHandler = sqlManager.getDbStyle().getKeyWordHandler();
        return keyWordHandler != null ? keyWordHandler.getCol(colName) : colName;
    }

    /****
     * 根据实体class获取表名
     * @param c
     * @return
     */
    public String getTableName(Class<?> c) {
    	if(this.tableName!=null){
    		return this.tableName;
		}
		String tname = sqlManager.getNc().getTableName(c);
    	if(asVirtual){
    		tname = sqlManager.getSqlTemplateEngine().runTemplate(tname,null);
		}


        TableDesc desc = sqlManager.getMetaDataManager().getTable(tname);
        String tableName2 = desc.getName();
        AbstractDBStyle style = (AbstractDBStyle) sqlManager.getDbStyle();
        tableName2 = style.getSQLTemplateEngine().wrapString(tableName2);
        if (desc.getSchema() != null) {
            return style.getKeyWordHandler().getTable(desc.getSchema()) + "." + style.getKeyWordHandler()
                    .getTable(tableName2);
        } else {
            return style.getKeyWordHandler().getTable(tableName2);
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
    public void addPreParam(List<Object> objects) {
        objects.addAll(params);
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
        //判断是否有效的变量
        if (value instanceof StrongValue) {
            if (!((StrongValue) value).isEffective()) {
                return;
            }
            value = ((StrongValue) value).getValue();
        } else if (value instanceof Optional) {
            if (!((Optional) value).isPresent()) {
                return;
            }
            value = ((Optional) value).get();
        }
        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        this.appendSql(link).appendSql(getCol(column)).appendSql(opt);
        if (value != null) {
            this.appendSql(" ? ");
            this.addParam(value);
        }
    }

	protected boolean isValidateValue(Object value){
		if (value instanceof StrongValue) {
			if (!((StrongValue) value).isEffective()) {
				return true;
			}else{
				return false;
			}

		} else if (value instanceof Optional) {
			if (!((Optional) value).isPresent()) {
				return true;
			}else{
				return false;
			}

		}

		return true;
	}



    protected void appendInSql(String column, Object value, String opt, String link) {
        //判断是否有效的变量
        if (value instanceof StrongValue) {
            if (!((StrongValue) value).isEffective()) {
                return;
            }
            value = ((StrongValue) value).getValue();
        } else if (value instanceof Optional) {
            if (!((Optional) value).isPresent()) {
                return;
            }
            value = ((Optional) value).get();
        }

        if (!(value instanceof Collection)) {
            throw new IllegalArgumentException("期望参数是Collection子类");
        }

        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        this.appendSql(link).appendSql(getCol(column)).appendSql(opt).appendSql(" ( ");

        for (Object o : (Collection<?>) value) {
            this.appendSql(" ? ,");
            this.addParam(o);
        }
        this.getSql().deleteCharAt(this.getSql().length() - 1);
        this.appendSql(" ) ");
    }

    protected void appendBetweenSql(String column, String opt, String link, Object... value) {
		if(!isValidateValue(value[0])||!isValidateValue(value[1])){
			return ;
		}

        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }

        this.appendSql(link).appendSql(getCol(column)).appendSql(opt).appendSql(" ? AND ? ");
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
    public Query<T> andLike(String column, Object value) {
        appendAndSql(column, value, "LIKE ");
        return (Query) this;
    }

    @Override
    public Query<T> andNotLike(String column, Object value) {
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
    public Query<T> andIn(String column, StrongValue value) {
        appendInSql(column, value, IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andIn(String column, Optional value) {
        appendInSql(column, value, IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andNotIn(String column, StrongValue value) {
        appendInSql(column, value, NOT_IN, AND);
        return (Query) this;
    }

    @Override
    public Query<T> andNotIn(String column, Optional value) {
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
    public Query<T> orLike(String column, Object value) {
        appendOrSql(column, value, "LIKE ");
        return (Query) this;
    }

    @Override
    public Query<T> orNotLike(String column, Object value) {
        appendOrSql(column, value, "NOT LIKE ");
        return (Query) this;
    }

    @Override
    public Query<T> orIsNull(String column) {
        appendOrSql(column, null, "IS NULL ");
        return (Query) this;
    }

    @Override
    public Query<T> orIsNotNull(String column) {
        appendOrSql(column, null, "IS NOT NULL ");
        return (Query) this;
    }

    @Override
    public Query<T> orIn(String column, Collection<?> value) {
        appendInSql(column, value, IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orIn(String column, StrongValue value) {
        appendInSql(column, value, IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orIn(String column, Optional value) {
        appendInSql(column, value, IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orNotIn(String column, Collection<?> value) {
        appendInSql(column, value, NOT_IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orNotIn(String column, StrongValue value) {
        appendInSql(column, value, NOT_IN, OR);
        return (Query) this;
    }

    @Override
    public Query<T> orNotIn(String column, Optional value) {
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

	@Override
	public Query<T> asTable(String tableName) {
		this.tableName = tableName;
		return (Query)this;
	}

	@Override
	public Query<T> virtualTable() {
    	asVirtual=true;
		return (Query)this;
	}

	private Query<T> manyCondition(QueryCondition condition, String link) {
        if (!(condition instanceof QueryCondition)) {
            throw new BeetlSQLException(BeetlSQLException.QUERY_CONDITION_ERROR, "连接条件必须是一个 QueryCondition 类型");
        }
        if (condition.getSql() == null || "".equals(condition.getSql().toString())) {
            return (Query) this;
        }
        //去除叠加条件中的WHERE
        int i = condition.getSql().indexOf(WHERE);
        if (i > -1) {
            condition.getSql().delete(i, i + 5);
        }

        if (getSql().indexOf(WHERE) < 0) {
            link = WHERE;
        }
        appendSql(link).appendSql(" (").appendSql(condition.getSql().toString()).appendSql(")");
        addParam(condition.getParams());
        return (Query) this;
    }

    @Override
    public StringBuilder getSql() {
        if (this.sql == null) {
            return new StringBuilder();
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

    @Override
    public Query<T> distinct() {
        this.distinct = true;
        return (Query) this;
    }
}
