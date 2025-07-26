package com.beetl.sql.rewrite;

import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.beetl.ext.fn.StringUtil;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.clazz.kit.StringKit;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 负责重写sql，实现多租户字段，逻辑删除字段，以及多表租户，多shcema租户等功能
 * @see ColRewriteParam
 * @see TableRewriteParam
 */
@Data
public class SqlRewriteInterceptor implements Interceptor {
	Logger logger = LoggerFactory.getLogger(SqlRewriteInterceptor.class);
	List<ColRewriteParam> rewriteConfigs = new ArrayList<>();
	TableRewriteParam tableRewriteParam;
	TableConfig tableCheck ;
	Map<String,String> sqlCache = null;
	static ThreadLocal<Integer> enableRewrite  = ThreadLocal.withInitial(() -> 0);

	public SqlRewriteInterceptor(SQLManager sqlManager,List<ColRewriteParam> rewriteConfigs){
		this(sqlManager,rewriteConfigs,null);
	}

	public SqlRewriteInterceptor(SQLManager sqlManager,List<ColRewriteParam> rewriteConfigs,TableRewriteParam tableRewriteParam){
		this.rewriteConfigs = rewriteConfigs;
		this.tableRewriteParam = tableRewriteParam;
		tableCheck = new DefaultTableConfig(sqlManager.getMetaDataManager());

	}


	public SqlRewriteInterceptor(SQLManager sqlManager){
		tableCheck = new DefaultTableConfig(sqlManager.getMetaDataManager());
	}

	public void enable(){
		enableRewrite.set(1);
	}

	public void reset(){
		enableRewrite.set(0);
	}

	@Override
	public void before(InterceptorContext ctx) {
		if(rewriteConfigs.isEmpty()&tableRewriteParam==null){
			return ;
		}

		if(enableRewrite.get()==0){
			return ;
		}

		String sql = ctx.getExecuteContext().sqlResult.jdbcSql;
		if(sqlCache!=null){
			String newSql = sqlCache.get(sql);
			if(newSql!=null){
				ctx.getExecuteContext().sqlResult.jdbcSql = newSql;
				return ;

			}
		}
		Statement statement ;
		try {
			 statement = (Statement) CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
		} catch (JSQLParserException e) {
			logger.error("parse error "+sql,e);
			throw new BeetlSQLException(BeetlSQLException.ERROR,"parse error "+sql,e);
		}

		SqlParserRewrite finder = new SqlParserRewrite(tableCheck, rewriteConfigs,tableRewriteParam);
		List<String> tables =  finder.getTableList(statement);
		String newSql = statement.toString();
		ctx.getExecuteContext().sqlResult.jdbcSql = newSql;

	}

	public Map<String, String> getSqlCache() {
		return sqlCache;
	}

	public void setSqlCache(Map<String, String> sqlCache) {
		this.sqlCache = sqlCache;
	}

	@Override
	public void after(InterceptorContext ctx) {

	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {

	}
}
