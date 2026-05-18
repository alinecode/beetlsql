package org.beetl.sql.jooq;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.jooq.AttachableQueryPart;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.List;

public class JooqHelper {
	SQLManager sqlManager;
	public JooqHelper(SQLManager sqlManager){
		this.sqlManager = sqlManager;
	}

	public DSLContext ctx(){
		DSLContext create = DSL.using(sqlManager.getDs().getMasterSource(), SQLDialect.H2);
		return create;
	}

	public <T> T  executeQueryOne(Class<T> clazz,AttachableQueryPart part){
		String jdbc = part.getSQL();
		List params = part.getBindValues();
		T  t = sqlManager.executeQueryOne(new SQLReady(jdbc,params.toArray()),clazz);
		return t;
	}

	public <T> List<T>   executeQuery(Class<T> clazz,AttachableQueryPart part ){
		String jdbc = part.getSQL();
		List params = part.getBindValues();
		List<T>  t = sqlManager.execute(new SQLReady(jdbc,params.toArray()),clazz);
		return t;
	}


}
