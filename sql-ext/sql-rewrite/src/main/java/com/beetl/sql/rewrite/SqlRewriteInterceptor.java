package com.beetl.sql.rewrite;

import lombok.Data;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SQLManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class SqlRewriteInterceptor implements Interceptor {
	Logger logger = LoggerFactory.getLogger(SqlRewriteInterceptor.class);
	List<ColRewriteParam> rewriteConfigs = new ArrayList<>();
	TableConfig tableCheck ;
	static ThreadLocal<Integer> enableRewrite  = ThreadLocal.withInitial(() -> 0);

	public SqlRewriteInterceptor(SQLManager sqlManager,List<ColRewriteParam> rewriteConfigs){
		this.rewriteConfigs = rewriteConfigs;
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
		if(rewriteConfigs.isEmpty()){
			return ;
		}

		if(enableRewrite.get()==0){
			return ;
		}

		String sql = ctx.getExecuteContext().sqlResult.jdbcSql;
		Statement statement ;
		try {
			 statement = (Statement) CCJSqlParserUtil.parse(sql, parser -> parser.withSquareBracketQuotation(true));
		} catch (JSQLParserException e) {
			logger.error("parse error "+sql,e);
			throw new BeetlSQLException(BeetlSQLException.ERROR,"parse error "+sql,e);
		}
		SqlParserRewrite finder = new SqlParserRewrite(tableCheck, rewriteConfigs);

		List<String> tables =  finder.getTableList(statement);
		String newSql = statement.toString();
		ctx.getExecuteContext().sqlResult.jdbcSql = newSql;

	}

	@Override
	public void after(InterceptorContext ctx) {

	}

	@Override
	public void exception(InterceptorContext ctx, Exception ex) {

	}
}
