package org.beetl.sql.core.page;

import java.util.List;

public interface PageResult<T> extends  java.io.Serializable {
     long getTotalRow();
     List<T> getList();
     long getTotalPage();
}
