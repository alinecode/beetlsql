package org.beetl.sql.core.extend;

import org.beetl.sql.core.ExecuteContext;

import java.util.Map;

/**
 * 执行任何模板sql语句的时候，附加的参数
 * 注意，这个不适用于SQLReady相关的API，因为SQLReady已经提供了SQL和参数，sqlManager负责执行和封装返回结果
 * @author lijiazhi
 */
public class ParaExtend {
    public Map morePara(ExecuteContext ctx){
        return  null;
    }
}
