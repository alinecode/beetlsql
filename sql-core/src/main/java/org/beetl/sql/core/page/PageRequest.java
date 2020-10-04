package org.beetl.sql.core.page;

import java.util.List;

/**
 * 分页请求，从1开始
 * @param <T>
 * @author xiandafu
 * @see DefaultPageRequest
 */
public interface PageRequest<T> {
    /**
     * 当前页数，从1开始
     * @return
     */
    public long getPageNumber();

    /**
     * 每页记录个数
     * @return
     */
    public int getPageSize();

    /**
     * 分页排序
     * @return
     */
    public String getOrderBy();

    /**
     * 有时候，分页请求不需要再次查询总数，这样可以节省一次sql查询，
     * @return
     */
    public boolean isTotalRequired();

    //不推荐，兼容beetlsql2
    default Object getParas(){
        return null;
    }

    /**
     * 得到查询起始位置
     * @param offsetStartZero
     * @return
     */
    default Object getStart(boolean offsetStartZero){
        Object start = (offsetStartZero ? 0 : 1) + (getPageNumber() - 1) * getPageSize();
        return start;
    }

    /**
     * 返回一个分页结果，用户可以实现自己的分页请求和分页结果
     * @param result
     * @return
     * @see DefaultPageResult
     */
    public PageResult of(List<T> result);
    public PageResult of(List<T> result,Long total);
}
