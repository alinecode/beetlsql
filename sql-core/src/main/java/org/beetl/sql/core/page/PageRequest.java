package org.beetl.sql.core.page;

import java.util.List;

/**
 * 分页请求，从1开始
 * @param <T>
 * @author xiandafu
 * @see DefaultPageRequest
 */
public interface PageRequest<T>  extends  java.io.Serializable{
    /**
     * 当前页数，从1开始
     * @return
     */
     long getPageNumber();

    /**
     * 每页记录个数
     * @return
     */
     int getPageSize();

    /**
     * 分页排序
     * @return
     */
     String getOrderBy();

    /**
     * 有时候，分页请求不需要再次查询总数，这样可以节省一次sql查询，
     * @return
     */
     boolean isTotalRequired();

    /**
     * 不进行列表查询<br/>
     * 对于比较慢的分页查询场景，有时我们会将列表和总数统计分为两个请求分别获取，
     * 这样可以分别请求列表或总数
     * @return
     */
     boolean isListRequired();

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
     PageResult of(List<T> result);
    public PageResult of(List<T> result,Long total);
}
