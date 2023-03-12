package org.beetl.sql.core.engine;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.SQLSource;
import org.beetl.sql.core.SqlId;
import org.beetl.sql.core.engine.template.BeetlTemplateEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 *  在Fetch功能中，控制哪些Fetch操作是允许的
 *  <pre>@{code
 *      public class User{
 *          private long id;
 *          private String name;
 *          privatre long departmentId;
 * 			@FetchOne(name="departmentId",enableOn="getDept")
 *          private Department dept
 *          @FetchMany(name="userId",enableOn="getaAcounts")
 *  *       private List<Account> accounts
 *      }
 *  }</pre>
 * <br/>
 * 在sql模板语句中，使用：fetchEnableOn("getDept")，这自有dept属性会被beetlsql从数据库中获取，accounts不会
 * 如果sql模板语句中，fetchEnableOn("getDept","getAccounts"),dept和accounts都会被额外抓取
 * @author xiandafu
 *
 */
public class DynamicFetchEnableOnFunction implements Function {
	public  static final String value = "xiuta"; //任意值
	@Override
	public Object call(Object[] paras, Context ctx) {
		ExecuteContext executeContext = (ExecuteContext) ctx.getGlobal("_executeContext");
		for(Object o:paras){
			if(o==null){
				continue;
			}
			//FetchOneAction.execute，过滤
			executeContext.setContextPara(o.toString(),value);
		}
		return null;
	}


}
