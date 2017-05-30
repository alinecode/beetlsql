package org.beetl.sql.core.engine;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 * 分页接口
 * 只要实现该接口, 用户可以定义自己的分页类.
 * 与一般框架分页类不同, 没有强耦合
 * </pre>
 * create time : 2017-05-28 19:15
 *
 * @author luoyizhu@gmail.com
 */
public interface Pageable<T> extends Serializable {

    List<T> getList();

    void setList(List<T> list);

    int getPageNumber();

    void setPageNumber(int pageNumber);

    int getPageSize();

    void setPageSize(int pageSize);

    int getTotalPage();

    void setTotalPage(int totalPage);

    int getTotalRow();

    void setTotalRow(int totalRow);
}
