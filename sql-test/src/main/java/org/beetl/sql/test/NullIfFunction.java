package org.beetl.sql.test;


import org.beetl.core.Context;
import org.beetl.sql.core.ExecuteContext;
import org.beetl.sql.core.db.DBType;

/**
 * BeetlTemplateEngine sqlTemplateEngine = (BeetlTemplateEngine)sqlManager.getSqlTemplateEngine();
 * sqlTemplateEngine.getBeetl().getGroupTemplate().registerFunction("nullif",new NullIfFunction());
 * 或者通过配置文件注册函数btsql-ext.properties
 * FN.nullif = xxx.NullIfFunction
 */

public class NullIfFunction implements org.beetl.core.Function {

    @Override
    public Object call(Object[] objects, Context context) {
		String col = (String)objects[0];
		ExecuteContext executeContext = (ExecuteContext)context.getGlobal("_executeContext");
		int dbType = executeContext.sqlManager.getDbStyle().getDBType();
		if(dbType== DBType.DB_MYSQL){
			return "NULLIF("+col+",'') ";
		}else if(dbType==DBType.DB_ORACLE){
			return "NVL(col"+",'')";
		}else{
			return col+" is null";
		}

    }
}
