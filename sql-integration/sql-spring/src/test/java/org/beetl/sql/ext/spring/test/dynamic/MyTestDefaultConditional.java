package org.beetl.sql.ext.spring.test.dynamic;

import org.beetl.sql.core.ConditionalSQLManager;
import org.beetl.sql.core.SQLManager;

import java.util.Map;

public class MyTestDefaultConditional extends ConditionalSQLManager.DefaultConditional {
    @Override
    public SQLManager decide(Class pojo, SQLManager defaultSQLManager, Map<String, SQLManager> sqlManagerMap){
        SQLManager sqlManager = super.decide(pojo,defaultSQLManager,sqlManagerMap);
        return sqlManager;
    }
}
