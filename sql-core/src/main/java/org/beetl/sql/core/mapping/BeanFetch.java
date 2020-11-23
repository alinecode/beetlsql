package org.beetl.sql.core.mapping;

import org.beetl.sql.core.ExecuteContext;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 一个在获取到数据库Bean后的回调，类似orm，用于自动获得更多的bean
 * @author xiandafu
 */
public interface BeanFetch {
    void fetchMore(ExecuteContext ctx, List beans, Annotation annotation);
}
