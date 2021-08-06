package org.beetl.sql.core.page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface PageResult<T> extends  java.io.Serializable {
     long getTotalRow();
     List<T> getList();
     long getTotalPage();
     void setList(List<T> list);

	default <R> PageResult<R> convert(Function<? super T, ? extends R> mapper) {
		List<R> collect = this.getList().stream().map(mapper).collect(Collectors.toList());
		PageResult page = this;
		page.setList(collect);
		return (PageResult<R>)page;
	}
}
