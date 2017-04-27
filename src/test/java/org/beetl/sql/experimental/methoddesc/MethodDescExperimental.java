package org.beetl.sql.experimental.methoddesc;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapper.MethodDesc;

import java.lang.reflect.Method;

/**
 * 用户处理自定义方法时的扩展 <BR>
 * create time : 2017-04-27 19:06
 *
 * @author luoyizhu@gmail.com
 */
public class MethodDescExperimental extends MethodDesc {

    @Override
    protected void doParse(SQLManager sm, Class entityClass, Method m, String sqlId) {
        super.doParse(sm, entityClass, m, sqlId);
    }
}
