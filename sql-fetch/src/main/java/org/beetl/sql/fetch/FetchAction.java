package org.beetl.sql.fetch;

import org.beetl.sql.core.ExecuteContext;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 完成fetch操作，execute方法内部需要先判断是否已经Fetch过，避免无限循环，参考{@code FetchAction}
 * @see FetchOneAction
 * @see FetchManyAction
 * @author xiandafu
 */
public interface FetchAction {
    public void execute(ExecuteContext ctx, List list);

    public void init(Class owner, Class target,Annotation config, PropertyDescriptor pd);
    public Annotation getAnnotation();
    public PropertyDescriptor getOriginProperty();

}
