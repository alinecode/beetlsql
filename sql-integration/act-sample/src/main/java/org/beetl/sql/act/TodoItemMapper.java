package org.beetl.sql.act;


import org.beetl.sql.mapper.BaseMapper;

import java.util.List;

public interface TodoItemMapper extends BaseMapper<TodoItem> {
	public List<TodoItem> selectAll();
}
