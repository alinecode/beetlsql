package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;

/**
 * 在sql执行前准备参数前，调用此API，得到一个新Bean，用于参数设定
 * @author xiandafu
 */
@Plugin
public interface BeanConvert {
    /**
     * 返回入库之前的对象
     * @param ctx
     * @param obj
     * @param an  注解信息，可以提供额外参数
     * @return
     */
     default Object before(ExecuteContext ctx, Object obj, Annotation an){
        return obj;
    }

    /**
     * 返回查询结果后的对象
     * @param ctx
     * @param obj
     * @param an
     * @return
     */
     default Object after(ExecuteContext ctx, Object obj, Annotation an){
        return obj;
    }

}
