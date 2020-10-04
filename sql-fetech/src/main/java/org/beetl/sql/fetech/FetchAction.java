package org.beetl.sql.fetech;

import org.beetl.sql.core.ExecuteContext;

import java.util.List;

/**
 * 完成fetch操作
 * @see FetchOneAction
 * @see FetchManyAction
 * @author xiandafu
 */
public interface FetchAction {
    public void execute(ExecuteContext ctx, List list);
}
