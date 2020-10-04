package org.beetl.sql.act;


import org.beetl.sql.annotation.entity.AutoID;
import org.beetl.sql.annotation.entity.Table;
import org.beetl.sql.mapper.BaseMapper;

import javax.persistence.Entity;
import javax.persistence.Id;

@Table(name = "TODOITEM")
@Entity
public class TodoItem  {

	@AutoID
	@Id
    private int id;
    private String desc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public interface Mapper extends BaseMapper<TodoItem> {
	}



}
