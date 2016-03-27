/**
 * 
 */
package org.beetl.sql.core.engine;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/** 用于翻页，要求sqlid必须具有page使用了page函数和pageTag,或者sqlId还有一个以$count 结尾的sqlId
 * @author suxj,xiandafu
 *
 */
public class PageQuery implements Serializable{

	private static final long serialVersionUID = -7523359884334787081L;
	public static String pageFlag = "_page";
	public static Object pageObj = new Object();
	
	protected List list;		//分页结果List
	protected Object paras ;   	//参数，pojo or map
	protected String orderBy ;	//排序
	
	protected long pageNumber;		//页数
	/**
	 * 默认的每页纪录总数，
	 */
	public static long 	DEFAULT_PAGE_SIZE = 20 ;
	protected   long pageSize = DEFAULT_PAGE_SIZE;		//每页记录数
	protected long totalPage;		//总页数
	protected long totalRow=-1;		//总行数,如果不为-1，则不需要再次查询
	
	
	public PageQuery(){
		this(1,null);
	}
	/** 
	 * @param pageNumber 页数
	 * @param paras 参数，pojo或者map
	 */
	public PageQuery(long pageNumber, Object paras){
		this.pageNumber = pageNumber;
		this.paras = paras;
	}
	
	/**
	 * 
	 ** @param pageNumber 页数
	 * @param paras 参数，pojo或者map
	 * @param userDefinedOrderBy 翻页字符串，如 create_date desc;  将自动增加到翻页语句里，这要求sqlId没有order by
	 */
	public PageQuery(long pageNumber, Object paras,String userDefinedOrderBy){
		this.pageNumber = pageNumber;
		this.paras = paras;
		this.orderBy = userDefinedOrderBy;
	}
	
	/**  
	 * @param pageNumber 页数，从1开始
	 * @param paras 参数
	 * @param totalRow 总行数，如果不为－1，则不需要beetlsq查询总行数
	 */
	public PageQuery(long pageNumber, Object paras,long totalRow){
		this(pageNumber,paras);
		this.totalRow = totalRow;
	}
	
	public PageQuery(long pageNumber, Object paras,String userDefinedOrderBy,long totalRow){
		this.pageNumber = pageNumber;
		this.paras = paras;
		this.orderBy = userDefinedOrderBy;
		this.totalRow =totalRow;
	}
	
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
	/**  
	 * @param pageNumber 页数
	 * @param paras 参数
	 * @param totalRow 总行数，如果不为－1，则不需要beetlsq查询总行数
	 * @param pageSize 每页行数
	 */
	public PageQuery(long pageNumber, Object paras,long totalRow,long pageSize){
		this(pageNumber,paras);
		this.totalRow = totalRow;
		this.pageSize = pageSize;
	}
	
	public List getList() {
		return list;
	}
	
	public long getPageNumber() {
		return pageNumber;
	}
	
	public long getPageSize() {
		return pageSize;
	}
	
	public long getTotalPage() {
		return totalPage;
	}
	
	public long getTotalRow() {
		return totalRow;
	}
	
	public boolean isFirstPage() {
		return pageNumber == 1;
	}
	
	public boolean isLastPage() {
		return pageNumber == totalPage;
	}

	public Object getParas() {
		return paras;
	}

	public void setParas(Object paras) {
		this.paras = paras;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	
	public void setTotalRow(long totalRow) {
		this.totalRow = totalRow;
	}
	
	
	public void setList(List list) {
		this.list = list;
		calcTotalPage();
	}

	
	
	public String getOrderBy() {
		return orderBy;
	}
	/** 如 name desc,create_date asc , 是数据库sql语句一部分
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	protected void calcTotalPage(){
		if(totalRow==0) this.totalPage= 1;
		else if(totalRow%this.pageSize==0){
			this.totalPage = totalRow/this.pageSize;
		}else{
			this.totalPage = totalRow/this.pageSize+1;
		}
	}

}
