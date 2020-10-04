package org.beetl.sql.core.page;

import lombok.Data;

import java.util.List;
@Data
public class DefaultPageResult<T> implements  PageResult<T> {

    long page;
    int pageSize;
    long totalRow;
    List<T> list;
    long totalPage;

    /**
     * 计算总共有多少页
     */
    protected void calcTotalPage() {
        if (totalRow == 0) {
            this.totalPage = 1;
        } else if (totalRow % this.pageSize == 0) {
            this.totalPage = totalRow / this.pageSize;
        } else {
            this.totalPage = totalRow / this.pageSize + 1;
        }
    }

}
