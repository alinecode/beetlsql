package org.beetl.sql.annotation.builder;

import org.beetl.sql.clazz.kit.Plugin;
import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 当操作对象有此注解的时候，会在sql执行前添加额外的参数
 * @author xiandafu
 */
@Plugin
public interface TargetAdditional {
    Map<String,Object> getAdditional(ExecuteContext ctx, Annotation an);
}
