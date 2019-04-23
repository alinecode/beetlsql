/**
 *
 */
package org.beetl.sql.core.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于翻页，要求sqlid必须具有page使用了page函数和pageTag,或者sqlId还有一个以$count 结尾的sqlId
 * 此类混合了查询翻页请求参数和查询结果<p></p>
 * 请求参数包括:
 * <ul>
 * <li>orderBy：动态传入的排序，sql形式</li>
 * <li>paras：查询参数，可以是map，或者Obj</li>
 * <li>pageNumber：第几页，从1开始</li>
 * </ul>
 * <p></p>
 * 查询结果包括
 * <ul>
 * <li>list：查询结果</li>
 * <li>totalRow：符合条件的总数</li>
 * <li>firstPage：是否是首页</li>
 * <li>lastPage：是否是尾页</li>
 * <li>totalPage：符合条件的总页数</li>
 * <li>totalPage：符合条件的总页数</li>
 * </ul>
 * <p></p>
 * 可以通过控制pageSize来确定每页记录条数<p></p>
 * 也可以实现设置totalRow为一个不是-1的数字来禁止beetlsql每次重新查询总数以提高性能
 *
 * @author suxj, xiandafu
 */
public class PageQuery<T> implements Serializable {

    private static final long serialVersionUID = -7523359884334787081L;
    public static String pageFlag = "_page";
    public static Object pageObj = new Object();

    protected List<T> list;        //分页结果List
    protected Object paras;    //参数，pojo or map
    protected String orderBy;    //排序

    protected long pageNumber;        //页数
    /**
     * 默认的每页纪录总数，
     */
    public static long DEFAULT_PAGE_SIZE = 20;
    protected long pageSize = DEFAULT_PAGE_SIZE;        //每页记录数
    protected long totalPage;        //总页数
    protected long totalRow = -1;        //总行数,如果不为-1，则不需要再次查询

    // 用于获得para
    private transient ParasBuilder parasBuilder = new ParasBuilder();

    public PageQuery() {
        this(1, null);
    }

    public PageQuery(long pageNumber) {
        this(pageNumber, null);
    }

    public PageQuery(long pageNumber, long pageSize) {
        this(pageNumber, null);
        this.pageSize = pageSize;
    }

    public PageQuery(long pageNumber, long pageSize, Object paras) {
        this(pageNumber, paras);
        this.pageSize = pageSize;
    }

    /**
     * @param pageNumber 页数
     * @param paras      参数，pojo或者map
     */
    public PageQuery(long pageNumber, Object paras) {
        this.pageNumber = pageNumber;
        this.paras = paras;
    }

    /**
     * * @param pageNumber 页数
     *
     * @param paras              参数，pojo或者map
     * @param userDefinedOrderBy 翻页字符串，如 create_date desc;  将自动增加到翻页语句里，这要求sqlId没有order by
     */
    public PageQuery(long pageNumber, Object paras, String userDefinedOrderBy) {
        this.pageNumber = pageNumber;
        this.paras = paras;
        this.orderBy = userDefinedOrderBy;
    }

    /**
     * @param pageNumber 页数，从1开始
     * @param paras      参数
     * @param totalRow   总行数，如果不为－1，则不需要beetlsq查询总行数
     */
    public PageQuery(long pageNumber, Object paras, long totalRow) {
        this(pageNumber, paras);
        this.totalRow = totalRow;
    }

    public PageQuery(long pageNumber, Object paras, String userDefinedOrderBy, long totalRow) {
        this.pageNumber = pageNumber;
        this.paras = paras;
        this.orderBy = userDefinedOrderBy;
        this.totalRow = totalRow;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @param pageNumber 页数
     * @param paras      参数
     * @param totalRow   总行数，如果不为－1，则不需要beetlsq查询总行数
     * @param pageSize   每页行数
     */
    public PageQuery(long pageNumber, Object paras, long totalRow, long pageSize) {
        this(pageNumber, paras);
        this.totalRow = totalRow;
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public long getPageSize() {
        return pageSize;
    }

    public long getTotalPage() {
        calcTotalPage();
        return totalPage;
    }

    public long getTotalRow() {
        return totalRow;
    }

    public boolean isFirstPage() {
        return pageNumber == 1;
    }

    public boolean isLastPage() {
        calcTotalPage();
        return pageNumber == totalPage;
    }

    public Object getParas() {
        return this.paras;
    }

    /**
     * 添加主参数，可以是map，pojo
     *
     * @param paras
     */
    public void setParas(Object paras) {
        parasBuilder.setRoot(paras);
        this.paras = parasBuilder.build();

    }

    /**
     * 添加额外参数，主参数变为有"_root"的Map
     *
     * @param key
     * @param value
     */
    public void setPara(String key, Object value) {
        parasBuilder.set(key,value);
        this.paras = parasBuilder.build();

    }


    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }


    public void setTotalRow(long totalRow) {
        this.totalRow = totalRow;
    }


    public void setList(List list) {
        this.list = list;
    }


    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 如 name desc,create_date asc , 是数据库sql语句一部分
     *
     * @param orderBy
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    /**
     * 计算页数，在取得查询结果，设置完totalRow后
     */
    private void calcTotalPage() {
        if (totalRow == 0) {
            this.totalPage = 1;
        } else if (totalRow % this.pageSize == 0) {
            this.totalPage = totalRow / this.pageSize;
        } else {
            this.totalPage = totalRow / this.pageSize + 1;
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderBy == null) ? 0 : orderBy.hashCode());
        result = prime * result + (int) (pageNumber ^ (pageNumber >>> 32));
        result = prime * result + (int) (pageSize ^ (pageSize >>> 32));
        result = prime * result + ((paras == null) ? 0 : paras.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PageQuery other = (PageQuery) obj;
        if (orderBy == null) {
            if (other.orderBy != null) {
                return false;
            }
        } else if (!orderBy.equals(other.orderBy)) {
            return false;
        }
        if (pageNumber != other.pageNumber) {
            return false;
        }
        if (pageSize != other.pageSize) {
            return false;
        }
        if (paras == null) {
            if (other.paras != null) {
                return false;
            }
        } else if (!paras.equals(other.paras)) {

            return false;
        }
        return true;
    }

    static class ParasBuilder{
        private Object root ;
        private Map map = null;
        public Object build(){
            if(map!=null&&root!=null){
                map.put("_root",root);
                return map;
            }else if(map==null&&root!=null){
                return root;
            }else if(map!=null&&root==null){
                return map;
            }else{
                return null;
            }
        }
        public void setRoot(Object o ){
            this.root = o;
        }
        public void set(String key,Object value){
            if(map==null){
                map = new HashMap();
            }
            map.put(key,value);
        }

    }
}
