package org.beetl.sql.core.page;

import java.util.List;

public interface PageResult<T> {
     long getTotalRow();
     List<T> getList();
     long getTotalPage();
}
