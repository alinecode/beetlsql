package org.beetl.sql.core.engine;


import java.util.List;

/**
 * <pre>
 * 框架内置的分页实现类
 * 用户也可以自定义分页对象(感觉内置的分页类功能不能满足你的), 只需要实现分页接口
 * </pre>
 * <pre>
 *     自定义方式:
 *     PageKit.setPageableBuilder(); 只需要设置这个方法.
 * </pre>
 * create time : 2017-05-28 19:19
 *
 * @author luoyizhu@gmail.com
 */
public class Page<T> implements Pageable<T> {
    private static final long serialVersionUID = -7523359884334787088L;

    private int pageNumber;
    private int pageSize;
    private int totalPage;
    private int totalRow;
    private List<T> list;

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getTotalPage() {
        return totalPage;
    }

    @Override
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public int getTotalRow() {
        return totalRow;
    }

    @Override
    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public boolean isFirstPage() {
        return pageNumber == 1;
    }

    public boolean isLastPage() {
        return pageNumber == totalPage;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalRow=" + totalRow +
                ", list=" + list +
                '}';
    }
}
