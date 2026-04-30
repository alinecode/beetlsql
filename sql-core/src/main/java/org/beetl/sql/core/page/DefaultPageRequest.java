package org.beetl.sql.core.page;

import lombok.Data;

import java.util.List;

/**
 * 自定义的一个PageReqeust请求
 * @param <T>
 */
@Data
public class DefaultPageRequest<T> implements PageRequest<T> {
	protected  long pageNumber;
	protected int pageSize = 20;
	protected String orderBy;
	protected boolean totalRequired;
	protected boolean listRequired;

	/**
	 * 从1开始
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static PageRequest of(long page,int pageSize){
		DefaultPageRequest request = new DefaultPageRequest();
		request.pageNumber = page;
		request.pageSize = pageSize;
		request.totalRequired = true;
		request.listRequired = true;
		return request;
	}

	public static PageRequest of(long page,int pageSize,boolean totalRequired){
		DefaultPageRequest request = new DefaultPageRequest();
		request.pageNumber = page;
		request.pageSize = pageSize;
		request.totalRequired = totalRequired;
		request.listRequired = true;
		return request;
	}

	public static PageRequest of(long page,int pageSize,boolean totalRequired,boolean listRequired){
		DefaultPageRequest request = new DefaultPageRequest();
		request.pageNumber = page;
		request.pageSize = pageSize;
		request.totalRequired = totalRequired;
		request.listRequired = listRequired;
		return request;
	}

	@Override
	public PageResult of(List<T> result) {
		DefaultPageResult pageResult = new DefaultPageResult();
		pageResult.setPage(pageNumber);
		pageResult.setPageSize(pageSize);
		pageResult.setList(result);
		pageResult.calcTotalPage();
		return pageResult;
	}

	@Override
	public PageResult of(List<T> result, Long total) {
		DefaultPageResult pageResult = new DefaultPageResult();
		pageResult.setTotalRow(total);
		pageResult.setList(result);
		pageResult.setPage(this.pageNumber);
		pageResult.setPageSize(this.pageSize);
		pageResult.calcTotalPage();
		return pageResult;

	}
}
