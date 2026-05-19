package org.beetl.sql.jooq;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.page.PageRequest;
import org.beetl.sql.core.page.PageResult;
import org.jooq.AttachableQueryPart;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Jooq 和 SQLManager集成，通过Jooq的拼接的SQL和参数，使用SQLManager API来执行
 * <pre>
 *	JooqHelper jooqHelper = new JooqHelper(sqlManager);
 *
 * 	List<ReLog> lists = jooqHelper.query(ReLog.class,
 * 		create -> create.select().from(ORDER_LOG).where(ORDER_LOG.VERSION.eq(100))
 * 	);
 *

 * </pre>
 *  你可以通过JOOQ代码生成表对应的类ORDER_LOG，或者使用JooqCodeGenHelper生成代码
 *  需要注意，由于jooq支持少量数据库，包括mysql，postgres，h2,clickhouse,MariaDB等，因此不是用所有的数据库能用此类，参考JooqCodeGenHelper.guessDbTypeByUrl了解支持的数据库
 *
 * @see "https://blog.jooq.org/simplifying-anti-join-with-jooq-syntax/"
 * @see "org.beetl.sql.jooq.JooqTest"
 */
public class JooqHelper {
	SQLManager sqlManager;
	public JooqHelper(SQLManager sqlManager){
		this.sqlManager = sqlManager;
	}


	/**
	 *
	 * @param clazz
	 * @param fun
	 * @return
	 * @param <T>
	 */
	public <T> T  queryOne(Class<T> clazz, CtxQueryFunction fun){
		DSLContext create = ctx();
		AttachableQueryPart part = fun.execute(create);
		String jdbc = part.getSQL();
		List params = part.getBindValues();
		T  t = sqlManager.executeQueryOne(new SQLReady(jdbc,params.toArray()),clazz);
		return t;
	}



	public <T> List<T>   query(Class<T> clazz , CtxQueryFunction fun){
		DSLContext create = ctx();
		AttachableQueryPart part = fun.execute(create);
		String jdbc = part.getSQL();
		List params = part.getBindValues();
		List<T>  t = sqlManager.execute(new SQLReady(jdbc,params.toArray()),clazz);
		return t;
	}

	public <T> PageResult<T>  queryPage(Class<T> clazz , PageRequest<T> pageRequest, CtxQueryFunction fun){
		DSLContext create = ctx();
		AttachableQueryPart part = fun.execute(create);
		String jdbc = part.getSQL();
		List params = part.getBindValues();
		PageResult<T> pageResult = sqlManager.execute(new SQLReady(jdbc,params.toArray()),clazz,pageRequest);
		return pageResult;
	}

	public DSLContext ctx(){
		DSLContext create = DSL.using(sqlManager.getDs().getMasterSource(), SQLDialect.H2);
		return create;
	}



}
