package org.beetl.sql.core.page;

import java.util.List;

public interface PageResult<T> {
    public long getTotalRow();
    public List<T> getList();
    public long getTotalPage();
}
